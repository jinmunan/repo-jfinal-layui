package com.cj.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseSysRole<M extends BaseSysRole<M>> extends Model<M> implements IBean {

	public void setId(String id) {
		set("id", id);
	}
	
	public String getId() {
		return getStr("id");
	}
	
	public void setDescript(String descript) {
		set("descript", descript);
	}
	
	public String getDescript() {
		return getStr("descript");
	}
	
	public void setIsStop(Integer isStop) {
		set("is_stop", isStop);
	}
	
	public Integer getIsStop() {
		return getInt("is_stop");
	}
	
	public void setOrgid(String orgid) {
		set("orgid", orgid);
	}
	
	public String getOrgid() {
		return getStr("orgid");
	}
	
	public void setParentId(String parentId) {
		set("parent_id", parentId);
	}
	
	public String getParentId() {
		return getStr("parent_id");
	}
	
	public void setRoleCode(String roleCode) {
		set("role_code", roleCode);
	}
	
	public String getRoleCode() {
		return getStr("role_code");
	}
	
	public void setRoleName(String roleName) {
		set("role_name", roleName);
	}
	
	public String getRoleName() {
		return getStr("role_name");
	}
	
	public void setUserCode(String userCode) {
		set("user_code", userCode);
	}
	
	public String getUserCode() {
		return getStr("user_code");
	}
	
	public void setVisitView(String visitView) {
		set("visit_view", visitView);
	}
	
	public String getVisitView() {
		return getStr("visit_view");
	}
	
}

