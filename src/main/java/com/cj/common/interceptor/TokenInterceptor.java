package com.cj.common.interceptor;

import com.cj.common.safe.TokenService;
import com.jfinal.aop.Inject;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;

/**
 * token拦截器
 *
 * @author THINKPAD
 */
public class TokenInterceptor implements Interceptor {

    @Inject
    TokenService tokenService;

    @Override
    public void intercept(Invocation invocation) {
        System.out.println("请求被TokenInterceptor拦截器拦截...........................");

        Controller controller = invocation.getController();
        String methName = invocation.getMethod().getName();

        // 给默认的添加、修改方法添加token
        // 因为add和edit方法只是跳转页面
        if (methName.equals("add") || methName.equals("edit")) {
            tokenService.createToken(controller);
        }

        //验证token
        if (methName.equals("save") || methName.equals("update")) {
            boolean flag = tokenService.validateToken(controller);
            if (!flag) {
                boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(controller.getHeader("X-Requested-With"));
                if (isAjax) {
                    controller.renderJson(Ret.fail("msg", "token验证不通过,请刷新页面"));
                } else {
                    controller.setAttr("msg", "token验证不通过");
                    controller.renderError(403);
                }
                return;
            }
            // 成功后才有的操作
            // 添加修改成功后，返回对的页面，此次是解决业务验证不通过的情况，
            // 如：添加用户时，如果存在用户编号，那么需要重新填写，此时就要重新赋值新的token
            tokenService.createToken(controller);
        }
        invocation.invoke();
    }
}
