package com.cj.common.module.login.controller;

import com.cj.common.util.util.VerifyCodeUtil;
import com.jfinal.core.Controller;
import com.jfinal.core.Path;

/**
 * 验证码
 * 将验证码设置到session中去
 *
 * @author THINKPAD
 */
@Path("/pub/verify")
public class VerifyController extends Controller {

    public void index() {
        //将验证码设置到session中去 session.setAttribute("xxx",yyy)
        getSession().setAttribute("verifyCode", VerifyCodeUtil.createImage(getResponse(), 1));
        renderNull();
    }
}
