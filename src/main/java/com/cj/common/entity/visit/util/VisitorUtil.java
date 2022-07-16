package com.cj.common.entity.visit.util;

import com.cj.common.entity.loginuser.ILoginUser;
import com.cj.common.entity.visit.Visitor;
import com.cj.common.model.SysUser;
import com.cj.common.util.kit.IpKit;
import com.cj.common.module.login.service.LoginService;
import com.jfinal.aop.Aop;
import com.jfinal.core.Controller;


import javax.servlet.http.HttpSession;

/**
 * 访问者工具
 *
 * @author THINKPAD
 */
@SuppressWarnings("all")
public class VisitorUtil {

    /**
     * key
     */
    public static final String VISITOR_KEY = "visitor";

    /**
     * 绑定本地线程
     */
    public static final ThreadLocal<ILoginUser> LOGIN_USER = new ThreadLocal<ILoginUser>();

    /**
     * 从本地线程获取登录用户
     */
    public static ILoginUser getLoginUser() {
        return LOGIN_USER.get();
    }

    /**
     * 设置用户到本地线程
     */
    public static void setLoginUser(ILoginUser loginUser) {
        if (LOGIN_USER.get() == null) {
            LOGIN_USER.set(loginUser);
        }
    }

    /**
     * 设置拜访者到session
     */
    public static void setVisitor(Visitor visitor, HttpSession session) {
        if (getVisitor(session) != null) {
            removeVisitor(session);
        }
        session.setAttribute(VISITOR_KEY, visitor);
    }

    /**
     * 获取访问者
     */
    public static Visitor getVisitor(HttpSession session) {
        if (session == null) {
            return null;
        }
        //为了解决偶尔出现的转换异常
        try {
            return (Visitor) session.getAttribute(VISITOR_KEY);
        } catch (Exception e) {
            e.printStackTrace();
            session.removeAttribute(VISITOR_KEY);
            return null;
        }
    }

    /**
     * 从session中移出拜访者
     */
    public static void removeVisitor(HttpSession session) {
        if (session != null) {
            session.removeAttribute(VISITOR_KEY);
        }
    }

    /**
     * 获取访问者的数据
     */
    @SuppressWarnings("unchecked")
    public static <T> T getVisitorUserData(HttpSession session) {
        if (session == null) {
            return null;
        }
        try {
            if (session.getAttribute(VISITOR_KEY) == null) {
                return null;
            } else {
                Visitor visitor = (Visitor) session.getAttribute(VISITOR_KEY);
                T t = (T) visitor.getUserData();
                return t;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取登录用户
     */
    @SuppressWarnings("unchecked")
    public static <T extends ILoginUser> T getUser(HttpSession session) {
        if (session == null) {
            return null;
        }
        try {
            if (session.getAttribute(VISITOR_KEY) == null) {
                return null;
            } else {
                Visitor visitor = (Visitor) session.getAttribute(VISITOR_KEY);
                T t = (T) visitor.getUserData();
                return t;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * cookie登陆
     */
    static LoginService loginService = Aop.get(LoginService.class);

    /**
     * 客户端每次请求都会携带cookie,cookie中存着sessionid等信息,可用于判断用户是否处于登录情况
     * 通过cookie获取登陆信息
     */
    public static Visitor getVisitor(Controller controller) {
        SysUser loginAccount = null;
        Visitor visitor = null;
        // 从cookie中获取sessionId
        String sessionId = controller.getCookie(LoginService.SESSION_ID_NAME);
        // 如果sessionid不存在
        if (sessionId == null) {
            // 就从session中获取sessionid
            sessionId = controller.getSessionAttr("sessionId");
        }

        //  如果sessionid存在
        if (sessionId != null) {
            // 从缓存中 获取具有sessionID的登录帐户信息
            loginAccount = loginService.getLoginAccountWithSessionId(sessionId);
            // 如果缓存中没有登录账号信息
            if (loginAccount == null) {
                // 获取登录ip
                String loginIp = IpKit.getRealIp(controller.getRequest());
                // 从数据库中回去登录账号信息
                loginAccount = loginService.loginWithSessionId(sessionId, loginIp);
            }
            if (loginAccount != null) {
                // 返回一个登录用户,其实就是将用户信息封装成一个登录用户
                visitor = loginService.getVisitor(loginAccount);
            } else {
                // cookie 登录未成功，证明该 cookie 已经没有用处，删之
                // 删除cookie和session
                controller.removeCookie(LoginService.SESSION_ID_NAME);
                controller.removeSessionAttr("sessionId");
            }
        }
        // 用于前端按钮权限控制,设置到请求域中 set()不明显,看不出来
        controller.setAttr("visitor", visitor);
        // 但会登录用户
        return visitor;
    }

    /**
     * cookie退出登陆
     */
    public static void removeVisitor(Controller controller) {
        // 从cookie中获取session
        String sessionId = controller.getCookie(LoginService.SESSION_ID_NAME);
        // 删除sessionid
        loginService.logout(sessionId);
        // 删除cookie
        controller.removeCookie(LoginService.SESSION_ID_NAME);
    }
}
