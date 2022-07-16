package com.cj.common.module.index;

import com.cj.common.base.controller.BaseController;
import com.cj.common.entity.visit.Visitor;
import com.cj.common.entity.visit.util.VisitorUtil;
import com.cj.common.model.SysUser;
import com.cj.common.module.login.service.LoginService;
import com.cj.common.module.portal.core.service.SysFuncService;
import com.cj.common.module.portal.core.service.SysUserService;
import com.cj.common.util.kit.Md5Kit;
import com.jfinal.aop.Inject;
import com.jfinal.core.NotAction;
import com.jfinal.core.Path;
import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;

/**
 * 系统首页
 * 当访问项目时的跳转页面接口 -> 用户没有登录 -> 跳转到login登录页面 session拦截器实现
 * 当用户登录后再次访问这个接口
 *
 * @author THINKPAD
 */
@Path("/")
public class IndexController extends BaseController {

    /**
     * 系统功能
     */
    @Inject
    SysFuncService sysFuncService;

    /**
     * 系统用户
     */
    @Inject
    SysUserService sysUserService;

    /**
     * 主页方法
     */
    public void index() {
        //屏蔽无效的url地址,getPara()==null
        if (getPara() != null) {
            getResponse().setStatus(404);
            renderError(404);
            return;
        }

        // 被锁屏幕的情况 如果刷新 跳转到登录页面
        if (this.isUnLock()) {
            logout();
            return;
        }

        // 渲染主页 系统功能服务
        // baseController中的方法 admin 和 null
        Record record = sysFuncService.getMenuInfo(getVisitor().getCode(), getPara("menuId"));
        // 设置到request域中
        setAttr("funcList", JsonKit.toJson(record.get("funcList")));
        setAttr("topMenuList", record.get("topMenuList"));
        setAttr("menuId", record.get("menuId"));
        setAttr("menu", record.get("menu"));
        render("index.html");
    }

    /**
     * 锁屏情况下
     * @return
     */
    @NotAction
    public boolean isUnLock() {
        // 锁屏未解锁,刷新浏览器强制移除登录身份信息
        String userName = (String) getSession().getAttribute(getVisitor().getName());
        if (userName != null) {
            // 移出session和cookie
            getSession().removeAttribute(userName);
            VisitorUtil.removeVisitor(this);
            return true;
        }
        return false;
    }

    /**
     * 退出登录
     */
    public void logout() {
        // 删除cookie
        VisitorUtil.removeVisitor(this);
        redirect("/pub/login");
    }

    /**
     * 锁屏 前端传过来一个userName参数,
     * 主要流程没有操作数据库
     *
     * @param # userName
     */
    public void lock() {
        String userName = getPara("userName", "userName");
        // 获取cookie中的登录用户,主要是判断
        Visitor visitor = VisitorUtil.getVisitor(this);
        if (visitor == null) {
            renderJson(err("登录信息已失效，请刷新浏览器(F5)重新登录"));
            return;
        }
        // 设置session域 一个userName的信息
        getSession().setAttribute(userName, userName);
        renderJson(suc());
    }


    /**
     * 解屏
     *
     * @param # userName
     * @param # userPasswd
     * @param # session
     */
    public void unLock() {
        // 判断登录用户是否存在,cookie和session是否过期
        Visitor visitor = VisitorUtil.getVisitor(this);
        if (visitor == null) {
            renderJson(err("登录信息已失效，请刷新浏览器(F5)重新登录"));
            return;
        }
        // 其实和登录差不多,但是这个密码没有被加密
        String passwd = Md5Kit.md5(getPara("password"));
        // visitor.getCode() 其实是getUserCode() 用户名
        SysUser user = sysUserService.findByUserCode(visitor.getCode());
        if (passwd.equals(user.getPasswd())) {
            // 密码相同 移出session域中的userName
            getSession().removeAttribute(getPara("userName"));
            renderJson(suc());
        } else {
            renderJson(err("密码错误，请重新输入..."));
        }
    }


    /**
     * 主题切换 居然要后端操控 post请求 反正jfinal也不分
     */
    public void switchTheme() {
        String theme = getPara("theme");
        // 还是校验用户是否过期
        Visitor visitor = VisitorUtil.getVisitor(this);
        // 获得用户id
        String id = visitor.getUserData().getId();
        if (sysUserService.updateTheme(id, theme)) {
            // 缓存移出用户信息?和sessionid信息
            CacheKit.remove(LoginService.LOGIN_ACCOUNT_CACHE_NAME, getCookie(LoginService.SESSION_ID_NAME));
            renderJson(suc("切换成功"));
        } else {
            renderJson(err("切换失败"));
        }
    }
}
