package com.cj.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseSysFunction<M extends BaseSysFunction<M>> extends Model<M> implements IBean {

	/**
	 * 主键
	 */
	public void setId(String id) {
		set("id", id);
	}
	
	/**
	 * 主键
	 */
	public String getId() {
		return getStr("id");
	}
	
	/**
	 * 功能名称
	 */
	public void setFuncName(String funcName) {
		set("func_name", funcName);
	}
	
	/**
	 * 功能名称
	 */
	public String getFuncName() {
		return getStr("func_name");
	}
	
	/**
	 * 是否启用(0:是,1:否)
	 */
	public void setIsStop(Integer isStop) {
		set("is_stop", isStop);
	}
	
	/**
	 * 是否启用(0:是,1:否)
	 */
	public Integer getIsStop() {
		return getInt("is_stop");
	}
	
	/**
	 * 功能url
	 */
	public void setLinkPage(String linkPage) {
		set("link_page", linkPage);
	}
	
	/**
	 * 功能url
	 */
	public String getLinkPage() {
		return getStr("link_page");
	}
	
	/**
	 * 上级编号
	 */
	public void setParentCode(String parentCode) {
		set("parent_code", parentCode);
	}
	
	/**
	 * 上级编号
	 */
	public String getParentCode() {
		return getStr("parent_code");
	}
	
	/**
	 * 上级名称
	 */
	public void setParentName(String parentName) {
		set("parent_name", parentName);
	}
	
	/**
	 * 上级名称
	 */
	public String getParentName() {
		return getStr("parent_name");
	}
	
	/**
	 * 功能类型(0:菜单,1:按钮)
	 */
	public void setFuncType(Integer funcType) {
		set("func_type", funcType);
	}
	
	/**
	 * 功能类型(0:菜单,1:按钮)
	 */
	public Integer getFuncType() {
		return getInt("func_type");
	}
	
	/**
	 * 图标
	 */
	public void setIcon(String icon) {
		set("icon", icon);
	}
	
	/**
	 * 图标
	 */
	public String getIcon() {
		return getStr("icon");
	}
	
	/**
	 * 排序
	 */
	public void setOrderNo(Integer orderNo) {
		set("order_no", orderNo);
	}
	
	/**
	 * 排序
	 */
	public Integer getOrderNo() {
		return getInt("order_no");
	}
	
	/**
	 * 注释
	 */
	public void setDescript(String descript) {
		set("descript", descript);
	}
	
	/**
	 * 注释
	 */
	public String getDescript() {
		return getStr("descript");
	}
	
	/**
	 * 是否展开菜单(0:展开,1:不展开)
	 */
	public void setSpread(Integer spread) {
		set("spread", spread);
	}
	
	/**
	 * 是否展开菜单(0:展开,1:不展开)
	 */
	public Integer getSpread() {
		return getInt("spread");
	}
	
	/**
	 * 是否在window页打开菜单（0:否,1:是）
	 */
	public void setIsWindowOpen(Integer isWindowOpen) {
		set("is_window_open", isWindowOpen);
	}
	
	/**
	 * 是否在window页打开菜单（0:否,1:是）
	 */
	public Integer getIsWindowOpen() {
		return getInt("is_window_open");
	}
	
}

