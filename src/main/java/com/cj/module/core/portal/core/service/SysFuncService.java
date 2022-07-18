package com.cj.module.core.portal.core.service;

import com.cj.common.base.service.BaseService;
import com.cj.common.model.SysFunction;
import com.cj.common.vo.TreeNode;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.IDataLoader;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 功能
 *
 * @author THINKPAD
 */
public class SysFuncService extends BaseService {

    private SysFunction dao = new SysFunction().dao();

    @Override
    public Model<?> getDao() {
        return dao;
    }

//    public Grid page(int pageNumber, int pageSize, Record record) {
//        return queryForListEq(pageNumber, pageSize, record, " order by func_type,order_no asc");
//    }


//    /**
//     * 新增功能同时授权给超级管理员
//     *
//     * @param entity
//     * @return
//     */
//    public boolean saveEntity(SysFunction entity) {
//        if (isExist(entity.getId())) {
//            return false;
//        }
//        return Db.tx(new IAtom() {
//
//            @Override
//            public boolean run() throws SQLException {
//                //系统权限
//                Db.update(Db.getSql("core.saveRoleFunction"), "sys_" + entity.getId(), entity.getId(), "sys");
//                //超级管理员权限
//                Db.update(Db.getSql("core.saveRoleFunction"), "superadmin_" + entity.getId(), entity.getId(), "superadmin");
//                return entity.save();
//            }
//        });
//    }
//
//    @Override
//    public boolean deleteById(String id) {
//        deleteRoleFuncByFunctionId(id);
//        return dao.deleteById(id);
//    }
//
//    @Override
//    public void deleteByIds(List<String> ids) {
//        Object[][] paras = new Object[ids.size()][1];
//        for (int i = 0; i < ids.size(); i++) {
//            paras[i][0] = ids.get(i);
//            deleteRoleFuncByFunctionId(ids.get(i));
//        }
//        String sql = "delete from " + getTable() + " where id=?";
//        Db.batch(sql, paras, 100);
//    }
//
//    public boolean isExist(String id) {
//        if (findById(id) != null) {
//            return true;
//        }
//        return false;
//    }
//
//    private void deleteRoleFuncByFunctionId(String functionId) {
//        String sql = "delete from sys_role_function where function_id=?";
//        Db.update(sql, functionId);
//    }
//
//    public Collection<TreeNode> getFunctionTree(String treeNodeId) {
//        List<Record> list = queryForList(Db.getSql("core.getFunctionTree"), treeNodeId);
//        Collection<TreeNode> nodes = new ArrayList<TreeNode>();
//        for (Record func : list) {
//
//            TreeNode node = new TreeNode();
//            if (func.getStr("icon") != null && func.getStr("icon").trim().length() > 0) {
//                node.setIcon("");
//            }
//            node.setId(func.getStr("id"));
//            node.setPid(func.getStr("parent_code"));
//            node.setText(func.getStr("func_name"));
//            node.setSpread(0 == func.getInt("spread") ? true : false);
//            node.setUrl(func.getStr("link_page"));
//            node.setIsWindowOpen(func.getInt("is_window_open"));
//            Collection<TreeNode> children = this.getFunctionTree(func.getStr("id"));
//            node.setChildren(children);
//            nodes.add(node);
//        }
//        return nodes;
//    }


    /**
     * 获取用户功能树
     * 第一次调用:admin sys  nodes列表
     * 第二次调用:admin sys_manger
     * 第三次调用:admin sys_func_manager
     * 第四次调用:admin sys_func_add
     * 结束 第四个nodes是null
     * 第一次回调:admin return nodes列表  称为第三次调用的孩子  nodes{node1:{aa,bb,cc:[null]},node2...}
     * 第二回调 nodes{node1:{aa,bb,cc:{nodes{node1:{aa,bb,cc:[null]},node2...}}},node2...}
     * 第三次回调 nodes{node1:{aa,bb,cc:{nodes{node1:{aa,bb,cc:{nodes{node1:{aa,bb,cc:[null]},node2...}}},node2...}}},node2...}
     * @param userCode   admin
     * @param treeNodeId frame_main_view 主页 sys
     * @return
     */
    public Collection<TreeNode> getUserFunctionTree(String userCode, String treeNodeId) {
        String sql = Db.getSql("core.getUserFunctionTree");
        List<Record> list = Db.find(sql, userCode, treeNodeId);

        List<TreeNode> nodes = new ArrayList<TreeNode>();
        for (int i = 0; i < list.size(); i++) {
            Record func = list.get(i);
            TreeNode node = new TreeNode();

            node.setId(func.getStr("id"));
            node.setPid(func.getStr("parent_code"));
            node.setText(func.getStr("func_name"));
            node.setUrl(func.getStr("link_page"));
            node.setIcon(func.getStr("icon"));
            //是否展开菜单(0:展开,1:不展开)
            node.setSpread(0 == func.getInt("spread") ? true : false);
            node.setIsWindowOpen(func.getInt("is_window_open"));
            // 递归
            Collection<TreeNode> children = this.getUserFunctionTree(userCode, func.getStr("id"));
            node.setChildren(children);
            nodes.add(node);
        }
        // 没有了触发return条件
        return nodes;
    }


//    public Collection<TreeNode> getRoleFunctionTree(String roleCode, String treeNodeId) {
//        String sql = Db.getSql("core.getRoleFunctionTree");
//        List<Record> list = Db.find(sql, roleCode, treeNodeId);
//
//        Collection<TreeNode> nodes = new ArrayList<TreeNode>();
//        for (int i = 0; i < list.size(); i++) {
//            Record func = list.get(i);
//            TreeNode node = new TreeNode();
//
//            node.setId(func.getStr("id"));
//            node.setPid(func.getStr("parent_code"));
//            node.setText(func.getStr("func_name"));
//            node.setUrl(func.getStr("link_page"));
//            node.setIcon(func.getStr("icon"));
//            node.setSpread(0 == func.getInt("spread") ? true : false);
//            node.setIsWindowOpen(func.getInt("is_window_open"));
//            Collection<TreeNode> children = this.getRoleFunctionTree(roleCode, func.getStr("id"));
//            node.setChildren(children);
//            nodes.add(node);
//        }
//        return nodes;
//    }
//
//

    /**
     * 获取系统菜单
     *
     * @param userCode admin
     * @param menuId   null
     */
    public Record getMenuInfo(String userCode, String menuId) {
        Record record = new Record();
        // 通过类似于mybatis.xml的方式配置sql语句
        String sql = Db.getSql("core.getUserFunctionTree");
        // 顶部菜单列表
        List<Record> topMenuList = Db.find(sql, userCode, "frame");
        //没有授权菜单
        if (topMenuList.size() == 0) {
            record.set("topMenuList", topMenuList);
            record.set("funcList", new ArrayList<>());
            record.set("menuId", null);
            record.set("menu", null);
            return record;
        }
        // 菜单id
        String topMenuId = topMenuList.get(0).getStr("id");
        // sysId
        String funcId = (menuId == null ? topMenuId : menuId);

        // 采集
        Collection<TreeNode> funcList = CacheKit.get("userFunc", "funcList" + userCode + funcId, new IDataLoader() {
            // 加载
            @Override
            public Object load() {
                // 获得用户功能树 递归
                return getUserFunctionTree(userCode, funcId);
            }
        });

        // 往响应对象设置参数和值
        record.set("topMenuList", topMenuList);
        record.set("funcList", funcList);
        //frame_main_view
        record.set("menuId", funcId);
        //主页对象
        record.set("menu", findById(funcId));
        return record;
    }
//
//    public Collection<Record> getXMSelectData(String treeNodeId, String selectData) {
//        List<Record> list = queryForList(Db.getSql("core.getFunctionTree"), treeNodeId);
//
//        Collection<Record> nodes = new ArrayList<Record>();
//        for (Record func : list) {
//            Record node = new Record();
//            node.set("name", func.get("func_name"));
//            node.set("value", func.get("id"));
//            if (func.get("id").equals(selectData)) {
//                node.set("selected", true);
//            }
//            Collection<Record> children = this.getXMSelectData(func.get("id"), selectData);
//            node.set("children", children);
//            nodes.add(node);
//        }
//        return nodes;
//    }
}
