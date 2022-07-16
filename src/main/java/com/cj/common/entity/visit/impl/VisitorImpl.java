package com.cj.common.entity.visit.impl;

import com.cj.common.entity.loginuser.ILoginUser;
import com.cj.common.entity.visit.Visitor;
import com.cj.common.model.SysOrg;

import java.util.Map;


/**
 * 访问者接口类
 *
 * @author THINKPAD
 */
@SuppressWarnings("all")
public class VisitorImpl implements Visitor {
    private static final long serialVersionUID = 1L;
    ILoginUser user;
    private long loginTime;
    private String orgName;
    private Integer type;
    private Map<String, Boolean> funcMap;
    private static final SysOrg orgDao = new SysOrg().dao();
    private String theme;

    public VisitorImpl(ILoginUser user) {
        super();
        loginTime = System.currentTimeMillis();
        this.user = user;
    }

    @Override
    public long getLoginTime() {
        return loginTime;
    }

    @Override
    public ILoginUser getUserData() {
        return user;
    }

    @Override
    public String getName() {
        return user.getUserName();
    }

    @Override
    public String getCode() {
        return user.getUserCode();
    }

    @Override
    public String getOrgName() {
        SysOrg sysOrg = orgDao.findById(user.getOrgId());
        if (sysOrg != null) {
            orgName = sysOrg.getOrgName();
        }
        return orgName;
    }

    @Override
    public Map<String, Boolean> getFuncMap() {
        return funcMap;
    }

    public void setFuncMap(Map<String, Boolean> funcMap) {
        this.funcMap = funcMap;
    }

    @Override
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }
}
