package com.cj.common.interceptor;

import com.cj.common.vo.Feedback;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import org.apache.log4j.Logger;

/**
 * 异常处理
 *
 * @author THINKPAD
 */
public class ExceptionInterceptor implements Interceptor {
    // 日志
    private static final Logger LOG = Logger.getLogger(ExceptionInterceptor.class);
    // 提示信息
    private static final String MSG = "msg";
    // 警告信息
    private static final String E = "e";

    @Override
    public void intercept(Invocation invocation) {
        System.out.println("请求被ExceptionInterceptor拦截器拦截...........................");

        // 获得控制器
        Controller controller = invocation.getController();
        // 获得action方法
        String methodName = invocation.getMethodName();

        try {
            // 执行调用
            // 向作用域中设置保存和修改的信息
            invocation.invoke();
            if ("save".equals(methodName)) {
                controller.setAttr(MSG, "数据保存成功");
            } else if ("update".equals(methodName)) {
                controller.setAttr(MSG, "数据修改成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error(e.getMessage(), e);
            // 等于忽略大小写
            // x-requested-with  XMLHttpRequest 表明是ajax异步
            boolean ajaxFlag = "XMLHttpRequest".equalsIgnoreCase(controller.getHeader("X-Requested-With"));
            if (ajaxFlag) {
                controller.renderJson(Feedback.error(e.getMessage() == null ? "系统异常" : e.getMessage()));
            } else if (controller.getResponse().getStatus() == 403) {
                controller.setAttr(E, e);
                controller.renderError(403);
            } else if (controller.getResponse().getStatus() == 404) {
                controller.renderError(404);
            } else {
                controller.getResponse().setStatus(500);
                controller.setAttr(E, e);
                controller.renderError(500);
            }
        }
    }
}
