package com.cj.module.core.login.controller;

import com.cj.common.base.controller.BaseController;
import com.cj.common.safe.TokenValidator;
import com.cj.common.util.kit.RSAKit;
import com.cj.module.core.login.service.LoginService;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.core.Path;
import com.jfinal.kit.Ret;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 登陆
 *
 * @author THINKPAD
 */
@Path("/pub/login")
public class LoginController extends BaseController {

    @Inject
    LoginService loginService;

//    @Inject
//    SysUserService sysUserService;

    /**
     * 参数是returnUrl
     */
    public void index() {
        // 初始化token的自定义缓存策略
        createToken();
        // 向request域中放值
        setAttr("returnUrl", encodeReturnUrl(getPara("returnUrl", "")));
        // 渲染跳转页面
        render("login.html");
    }

    /**
     * 登录认证
     * 登录的时候前端已经对密码进行了加密
     */
    @Before(TokenValidator.class)
    public void submit() {
        // 用户名
        String userCode = getPara("userCode");
        // 51151223323412333123121243112256122312315123123123
        String password = getPara("password");
        // returnUrl = "/"
        String returnUrl = encodeReturnUrl(getPara("returnUrl", ""));

        try {
            // 对密码解密
            password = RSAKit.decryptionToString(password);
            // 验证登录 ret作为返回对象
            Ret ret = loginService.aopLogin(userCode, password, getRequest());

            if (ret.isOk()) {
                String sessionId = ret.getStr(LoginService.SESSION_ID_NAME);
                int maxAgeInSeconds = ret.getInt("maxAgeInSeconds");
                // 设置cookie session信息 cookie保存在客户端
                setCookie(LoginService.SESSION_ID_NAME, sessionId, maxAgeInSeconds, true);
                // 登陆日志需要 设置到session域中
                setSessionAttr("sessionId", sessionId);
            }
            // 重定向到 "/" 首页 index.html
            redirect(!"".equals(returnUrl) ? returnUrl : "/");
        } catch (Exception e) {
            //捕获异常
            handerException(e);
            //显示验证码
            // 这一步只是单纯的因为输错密码而是验证码展示出来
            setAttr("verifyCode", "verifyCode");
            setAttr("returnUrl", returnUrl);
            setAttr("msg", e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
            createToken();
            render("login.html");
        }
    }


    /**
     * 中文转码 返回网址
     * Pattern p=Pattern.compile("([a-z]+)(\\d+)");  ===>[[a-z]+]=>[字母至少出现一次]  [\\d+]=>[数字至少出现一次]
     * Matcher m=p.matcher("aaa2223bb");
     * m.find();            //匹配aaa2223
     * m.groupCount();      //返回2,因为有2组
     * m.start(1);          //返回0 返回第一组匹配到的子字符串在字符串中的索引号
     * m.start(2);          //返回3
     * m.end(1);            //返回3 返回第一组匹配到的子字符串的最后一个字符在字符串中的索引位置.
     * m.end(2);            //返回7
     * m.group(1);          //返回aaa,返回第一组匹配到的子字符串
     * m.group(2);          //返回2223,返回第二组匹配到的子字符串
     */
    @SuppressWarnings("all")
    private String encodeReturnUrl(String returnUrl) {
        //匹配中文 【\u4e00-\u9fa5】判断是不是中文的一个条件
        Pattern pattern = Pattern.compile("[\\u4e00-\\u9fa5]");
        Matcher matcher = pattern.matcher(returnUrl);
        //中文转码 如果匹配到了
        while (matcher.find()) {
            try {
                // 将Url中的中文转为为UTF-8
                returnUrl = returnUrl.replace(matcher.group(), URLEncoder.encode(matcher.group(), "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return returnUrl;
    }


//    /**
//     * 退出登录
//     */
//    public void logout() {
//        loginService.logout(getCookie(LoginService.sessionIdName));
//        removeCookie(LoginService.sessionIdName);
//        redirect("/pub/login");
//    }
}
