package com.cj.common.entity.loginuser.impl;


import com.cj.common.entity.loginuser.ILoginUser;

/**
 * 实现类
 *
 * @author THINKPAD
 */
@SuppressWarnings("all")
public class LoginUserImpl implements ILoginUser {
    private static final long serialVersionUID = 1L;
    private String id;
    private String userCode;
    private String userName;
    private String orgId;
    private String company;
    private String ip;
    private String password;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getUserCode() {
        return userCode;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public String getOrgId() {
        return orgId;
    }

    @Override
    public String getIp() {
        return ip;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }
}
