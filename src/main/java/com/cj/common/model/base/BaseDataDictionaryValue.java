package com.cj.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseDataDictionaryValue<M extends BaseDataDictionaryValue<M>> extends Model<M> implements IBean {

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
	 * 数据值
	 */
	public void setValue(String value) {
		set("value", value);
	}
	
	/**
	 * 数据值
	 */
	public String getValue() {
		return getStr("value");
	}
	
	/**
	 * 数据名称
	 */
	public void setName(String name) {
		set("name", name);
	}
	
	/**
	 * 数据名称
	 */
	public String getName() {
		return getStr("name");
	}
	
	/**
	 * 排序
	 */
	public void setOrderNum(Integer orderNum) {
		set("order_num", orderNum);
	}
	
	/**
	 * 排序
	 */
	public Integer getOrderNum() {
		return getInt("order_num");
	}
	
	/**
	 * 备注
	 */
	public void setRemark(String remark) {
		set("remark", remark);
	}
	
	/**
	 * 备注
	 */
	public String getRemark() {
		return getStr("remark");
	}
	
	/**
	 * 字典编号
	 */
	public void setDictionaryCode(String dictionaryCode) {
		set("dictionary_code", dictionaryCode);
	}
	
	/**
	 * 字典编号
	 */
	public String getDictionaryCode() {
		return getStr("dictionary_code");
	}
	
}

