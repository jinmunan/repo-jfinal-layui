package com.cj.common.model;

import com.cj.common.model.base.BaseSession;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
//@Table(tableName="session",primaryKey="id")
public class Session extends BaseSession<Session> {
    private static final long serialVersionUID = 1L;
    public static final Session dao = new Session().dao();

    /**
     * 登录会话是否已过期
     */
    public boolean isExpired() {
        return getExpiretTime() < System.currentTimeMillis();
    }

    /**
     * 登录会话是否未过期
     */
    public boolean notExpired() {
        return !isExpired();
    }
}
