package com.cj.module.core.login.exception;

/**
 * @author THINKPAD
 */
public class LoginException extends Exception {
    private static final long serialVersionUID = 1L;
    private static final String msg = "帐号或密码错误";

    public LoginException() {
        super("帐号或密码错误");
    }

    public LoginException(String message) {
        super(message);
    }

    public String getMsg() {
        return msg;
    }
}
