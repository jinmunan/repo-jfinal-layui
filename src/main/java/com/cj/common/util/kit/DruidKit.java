//package com.cj.common.kit;
//
//import com.jfinal.aop.Aop;
//import com.jfinal.plugin.druid.DruidStatViewHandler;
//import com.jfinal.plugin.druid.IDruidStatViewAuth;
//import com.qinhailin.common.model.SysUser;
//import com.qinhailin.pub.login.service.LoginService;
//
//import javax.servlet.http.Cookie;
//import javax.servlet.http.HttpServletRequest;
//
///**
// * 创建 DruidStatViewHandler 的工具类
// *
// * 可通过 "/assets/druid" 访问到 druid 提供的 sql 监控与统计功能
// * 方便找到慢 sql，进而对慢 sql 进行优化
// * 注意：这里的访问路径是下面代码中指定的，可以设置为任意路径
// *
// * 注意 druid 监控模块中使用的静态资源文件如 .html .css 被打包在了 druid 的jar 包之中
// * 如果你的项目在前端有 nginx 代理过了这些静态资源，需要将这些资源解压出来并放到
// * 正确的目录下面
// *
// * 具体到该配置中的 url 为 "/assets/druid"，那么相关静态资源需要解压到该目录之下
// */
//public class DruidKit {
//
//	static LoginService loginService=Aop.get(LoginService.class);
//
//	public static DruidStatViewHandler getDruidStatViewHandler() {
//		return  new DruidStatViewHandler("/assets/druid", new IDruidStatViewAuth() {
//			public boolean isPermitted(HttpServletRequest request) {
//				String sessionId = getCookie(request, LoginService.sessionIdName);
//				if (sessionId != null) {
//					SysUser user = loginService.getLoginAccountWithSessionId(sessionId);
//					if(user!=null)
//						return "admin".equals(user.getUserCode())||"superadmin".equals(user.getUserCode());
//				}
//				return false;
//			}
//		});
//	}
//
//	public static String getCookie(HttpServletRequest request, String name) {
//		Cookie cookie = getCookieObject(request, name);
//		return cookie != null ? cookie.getValue() : null;
//	}
//
//	private static Cookie getCookieObject(HttpServletRequest request, String name) {
//		Cookie[] cookies = request.getCookies();
//		if (cookies != null)
//			for (Cookie cookie : cookies)
//				if (cookie.getName().equals(name))
//					return cookie;
//		return null;
//	}
//}
