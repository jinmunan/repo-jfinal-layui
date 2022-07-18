package com.cj.module.core.portal.core.controller;

import com.cj.common.base.controller.BaseController;
import com.cj.common.model.SysUser;
import com.cj.module.core.portal.core.service.SysUserService;
import com.jfinal.aop.Inject;
import com.jfinal.core.Path;

/**
 * @version 1.0
 * @author： jinmunan
 * @date： 2022-07-17 13:12
 */
@Path("/portal/core/sysUser")
public class SysUserController extends BaseController {
    @Inject
    SysUserService sysUserService;
//    @Inject
//    SysOrgService sysOrgService;
//    @Inject
//    SysUserRoleService sysUserRoleService;

//    public void index() {
//        setAttr("orgList", service.queryOrgIdAndNameRecord());
//        render("index.html");
//    }

    /**
     * 展示用户页面
     */
    public void my() {
        createToken();
        SysUser entity = sysUserService.findByUserCode(getVisitor().getCode());
        setAttr("sysUser", entity);
//        SysOrg org = (SysOrg) sysOrgService.findById(entity.getOrgId());
//        setAttr("orgName", org.getOrgName());
        render("my/index.html");
    }
}
