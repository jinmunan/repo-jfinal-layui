package com.cj.common.entity.visit;

import com.cj.common.entity.loginuser.ILoginUser;

import java.io.Serializable;
import java.util.Map;


/**
 * 访问者接口
 *
 * @author
 */
@SuppressWarnings("all")
public interface Visitor extends Serializable {
    /**
     * 获取登录时间
     */
    public long getLoginTime();

    /**
     * 获取用户数据
     */
    public ILoginUser getUserData();

    /**
     * 获取名字
     */
    public String getName();

    /**
     * 获取代码
     */
    public String getCode();

    /**
     * 获取组织名
     */
    public String getOrgName();

    /**
     * 获取类别
     */
    public Integer getType(); // 获取用户类型

    /**
     * 获取功能图
     */
    public Map<String, Boolean> getFuncMap();

    /**
     * 获取登录主题
     */
    public String getTheme();
}
