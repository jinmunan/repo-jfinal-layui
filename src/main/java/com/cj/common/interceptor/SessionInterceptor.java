package com.cj.common.interceptor;

import com.cj.common.entity.visit.Visitor;
import com.cj.common.entity.visit.util.VisitorUtil;
import com.jfinal.aop.Inject;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.Ret;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 登陆session、权限认证拦截器
 *
 * @author THINKPAD
 * LoginInterceptor
 */
public class SessionInterceptor implements Interceptor {

//    @Inject
//    SysRoleFuncService sysRoleFuncService;
//
//    @Inject
//    SysUserService sysUserService;

    @Override
    public void intercept(Invocation invocation) {
        System.out.println("请求被SessionInterceptor拦截器拦截...........................");
        // 获取控制器
        Controller controller = invocation.getController();
        // 获取方法路由键 => "/"
        String actionKey = invocation.getActionKey();
        // 通过cookie获取登陆信息
        Visitor visitor = VisitorUtil.getVisitor(controller);
        // ajax请求
        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(controller.getHeader("X-Requested-With"));

        //需要身份认证的地址 以...开始
        if (!actionKey.startsWith("/pub")) {
            if (visitor == null) {
                // 地址参数 "/" 无参数
                String para = controller.getPara() == null ? "" : "/" + controller.getPara();
                Map<String, String[]> paramMap = controller.getParaMap();
                boolean first = true;
                for (String key : paramMap.keySet()) {
                    para += first ? "?" : "&";
                    para += key + "=" + paramMap.get(key)[0];
                    first = false;
                }
                //参数转码
                try {
                    para = URLEncoder.encode(para, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                //部门详情
                if (actionKey.equals("/portal/core/sysOrg/detail")) {
                    actionKey = "/portal/core/sysOrg";
                }
                //公共路由
                if (actionKey.equals("/portal/go")) {
                    actionKey += "/" + controller.getRequest().getAttribute("view");
                }
                //重新登录，携带重定向地址
                if (isAjax) {
                    controller.renderJson(Ret.fail("msg", "身份失效，请重新登录！"));
                    return;
                }
                //重定向到登录页面
                controller.redirect("/pub/login?returnUrl=" + actionKey + para);
                return;
            }

//            //验证用户权限
//            if (sysRoleFuncService.getSysPermissions().get(actionKey) != null
//                    && sysRoleFuncService.getUserPermissions(vs.getCode()).get(actionKey) == null) {
//
//                if (isAjax) {
//                    controller.renderJson(Ret.fail("msg", "没有访问权限，请联系管理员！"));
//                    return;
//                }
//                controller.getResponse().setStatus(403);
//                controller.renderError(403);
//                return;
//            }

        }

//        //根据用户theme主题发送前端
//        if (visitor != null)
//            controller.set("theme", visitor.getTheme());
//
//
//        //禁用系统所有的数据操作
//        if (PropKit.getBoolean("disableDatabaseOperations", false)) {
//            String method = "save.*|update.*|delete.*|upload*|resetPassword.*|is.*|";
//            String methodName = invocation.getMethodName();
//            Pattern pattern = Pattern.compile(method);
//            boolean b = pattern.matcher(methodName).matches();
//            if (b) {
//                b = "XMLHttpRequest".equalsIgnoreCase(controller.getHeader("X-Requested-With"));
//                if (b) {
//                    controller.renderJson(Ret.fail("code", "error").set("msg", "没有访问权限，请联系管理员"));
//                    return;
//                } else {
//                    controller.getResponse().setStatus(403);
//                    controller.renderError(403);
//                }
//            }
//        }

        invocation.invoke();
    }
}
