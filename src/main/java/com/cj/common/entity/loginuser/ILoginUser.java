package com.cj.common.entity.loginuser;

/**
 * 保存登录帐号用户ID，帐号名
 *
 * @author THINKPAD
 */
@SuppressWarnings("all")
public interface ILoginUser extends IUser {

    /**
     * 用户ID
     */
    @Override
    public String getId();

    /**
     * 登陆账号
     */
    @Override
    public String getUserCode();

    /**
     * 密码
     */
    @Override
    public String getPassword();

    /**
     * 用户名称
     */
    @Override
    public String getUserName();

    /**
     * 获取组织id
     */
    @Override
    public String getOrgId();

    /**
     * 获取所在企业ID
     */
    public String getCompany();

    /**
     * 登录用户IP
     */
    public String getIp();

}
