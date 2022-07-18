package com.cj.module.core.portal.core.service;

import com.cj.common.base.service.BaseService;
import com.jfinal.plugin.activerecord.Model;

/**
 * 部门
 *
 * @author qinhailin
 */
public class SysOrgService extends BaseService {

    @Override
    public Model<?> getDao() {
        return null;
    }

//	private SysOrg dao = new SysOrg().dao();
//
//	/* (non-Javadoc)
//	 * @see com.qinhailin.common.base.service.BaseService#getDao()
//	 */
//	@Override
//	public Model<?> getDao() {
//		return dao;
//	}
//
//	/**
//	 * 删除部门及下级部门,多层会有数据冗余
//	 * @param id
//	 * @author QinHaiLin
//	 * @date 2018年12月5日
//	 */
//	public void deleteOrgById(String id) {
//		if(id.equalsIgnoreCase("sys")){
//			return;
//		}
//		String sql = "delete from sys_org where id=? or parentid=?";
//		Db.update(sql, id, id);
//		sql="update sys_user set org_id='sys' where org_id=?";
//		Db.update(sql,id);
//	}
//
//	public Collection<TreeNode> getOrgTree(String treeNodeId) {
//		List<SysOrg> list = dao.find("select * from sys_org where parentid=? order by id asc", treeNodeId);
//
//		Collection<TreeNode> nodes = new ArrayList<TreeNode>();
//		for (SysOrg org : list) {
//			TreeNode node = new TreeNode();
//			node.setId(org.getId());
//			node.setPid(org.getParentid());
//			node.setText(org.getOrgName());
//			Collection<TreeNode> children = this.getOrgTree(org.getId());
//			node.setChildren(children);
//			nodes.add(node);
//		}
//		return nodes;
//	}
//
//	/**
//	 * 修改关联的部门名称
//	 * @param id
//	 * @author QinHaiLin
//	 * @date 2019-02-23
//	 */
//	public void updateOrgParentName(String id){
//		String sql="update sys_org as a inner join (select id,org_name from sys_org where id=?) as b on a.parentid=b.id set a.parentid_name=b.org_name";
//		Db.update(sql, id);
//	}
//
//	/**
//	 * 获取下级部门id
//	 * @param orgId
//	 * @return
//	 */
//	public String getIdsByOrgId(String orgId,StringBuffer sbf){
//		Collection<TreeNode> treeList=this.getOrgTree(orgId);
//		for(TreeNode t:treeList){
//			if(sbf.length()>0)sbf.append(",");
//			sbf.append("'").append(t.getId()).append("'");
//			getIdsByOrgId(t.getId(),sbf);
//		}
//		return sbf.toString();
//	}
//
//	/**
//	 * 部门下拉函数接口
//	 * @param treeNodeId
//	 * @param selectData，选中项数据
//	 * @param disabledData 禁用选项数据
//	 * @return
//	 */
//	public Collection<Record> getXMSelectData(String treeNodeId,String selectData,String disabledData) {
//		String[] selData={};
//		String[] disData={};
//		if(selectData!=null){
//			selData=selectData.split(",");
//		}
//		if(selectData!=null){
//			disData=disabledData.split(",");
//		}
//
//		List<SysOrg> list = dao.find("select * from sys_org where parentid=? order by id asc", treeNodeId);
//		Collection<Record> nodes = new ArrayList<Record>();
//		for (SysOrg org : list) {
//			Record node = new Record();
//			node.set("name",org.getOrgName());
//			node.set("value",org.getId());
//
//			//禁用选项
//			if(disData.length>0){
//				for (int i = 0; i < disData.length; i++) {
//					if(org.getId().equals(disData[i])){
//						node.set("disabled", true);
//					}
//				}
//			}
//			//选中值，多选、单选
//			if(selData.length>1){
//				for (int i = 0; i < selData.length; i++) {
//					if(org.getId().equals(selData[i])){
//						node.set("selected", true);
//					}
//				}
//			}else{
//				if(org.getId().equals(selectData)){
//					node.set("selected", true);
//				}
//			}
//
//			Collection<Record> children = this.getXMSelectData(org.getId(),selectData,disabledData);
//			node.set("children",children);
//			nodes.add(node);
//		}
//
//		return nodes;
//	}
}
