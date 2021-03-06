package com.cj.module.core.portal.core.service;

import com.cj.common.base.service.BaseService;
import com.cj.common.model.SysUser;
import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Model;

/**
 * 用户
 *
 * @author THINKPAD
 */
public class SysUserService extends BaseService {

    @Inject
    private SysOrgService orgService;

    private SysUser dao = new SysUser().dao();

    @Override
    public Model<?> getDao() {
        return dao;
    }

//    public Grid page(int pageNumber, int pageSize, Record record) {
//        Record rd = new Record();
//        rd.set("a.user_code like", record.getStr("userCode"));
//        rd.set("a.user_name like", record.getStr("userName"));
//        rd.set("a.sex=", record.getStr("sex"));
//        String sql = Db.getSql("core.getUserList");
//        String orgId = record.getStr("orgId");
//
//        //部门用户列表
//        String type = record.getStr("type");
//        if ("org".equals(type)) {
//
//            StringBuffer sbf = new StringBuffer();
//            sbf.append("'").append(orgId).append("'");
//            String orgIds = orgService.getIdsByOrgId(orgId, sbf);
//
//            sql = Db.getSql("core.getOrgUserList").replace("?", orgIds);
//            return queryForList(sql, pageNumber, pageSize, rd, null);
//        }
//        //用户管理列表
//        rd.set("a.org_id=", orgId);
//        return queryForList(sql, pageNumber, pageSize, rd, null);
//    }
//
//    /**
//     * 部门下拉选择数据
//     * [{value:id,text:orgname}]
//     *
//     * @return
//     */
//    public List<Record> queryOrgIdAndNameRecord() {
//        return queryForList("select id value,org_name text from sys_org where isstop=1");
//    }

    /**
     * 通过用户名查找用户
     * select * from tb where user_code = userCode
     *
     * @param userCode 用户名
     * @return 用户
     */
    public SysUser findByUserCode(String userCode) {
        return (SysUser) findByPk("user_code", userCode.toLowerCase());
    }

//    public boolean saveEntity(SysUser entity) {
//        if (isExist(entity.getUserCode())) {
//            return false;
//        }
//        entity.setPasswd(Md5Kit.md5(entity.getPasswd()));
//        if (entity.getOrgId() == null) {
//            entity.setOrgId("sys");
//        }
//        return entity.save();
//    }
//
//    @Override
//    public boolean deleteById(String id) {
//        id = id.toLowerCase();
//        if (!"admin".equals(id) && !"superadmin".equals(id)) {
//            return dao.deleteById(id);
//        }
//        return false;
//    }
//
//    @Override
//    public void deleteByIds(List<String> ids) {
//        Object[][] paras = new Object[ids.size()][1];
//        for (int i = 0; i < ids.size(); i++) {
//            if (!"superadmin".equals(ids.get(i).toLowerCase())
//                    && !"admin".equals(ids.get(i).toLowerCase())) {
//                paras[i][0] = ids.get(i);
//            }
//        }
//        String sql = "delete from sys_user where id=?";
//        Db.batch(sql, paras, ids.size());
//    }
//
//    public boolean isExist(String userCode) {
//        if (findByUserCode(userCode) != null) {
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * 默认密码:用户编号+123
//     *
//     * @param ids
//     * @author QinHaiLin
//     * @date 2018年12月5日
//     */
//    public void resetPassword(List<String> ids) {
//        for (String id : ids) {
//            id = id.toLowerCase();
//            if (!id.equals("superadmin") && !id.equals("admin")) {
//                SysUser entity = (SysUser) findById(id);
//                entity.setPasswd(Md5Kit.md5(entity.getUserCode() + "123"));
//                entity.setAllowLoginTime(new Date());
//                entity.update();
//            }
//        }
//    }
//
//    public boolean updatePassword(String userCode, String newPassword, String oldPassword) {
//        SysUser entity = findByUserCode(userCode);
//        if (!entity.getPasswd().equals(Md5Kit.md5(oldPassword))) {
//            return false;
//        }
//        entity.setPasswd(Md5Kit.md5(newPassword));
//        return entity.update();
//    }
//
//    /**
//     * 获取用户功能权限map集合
//     *
//     * @param userCode
//     * @return
//     */
//    public Map<String, Boolean> getUserFuncMap(String userCode) {
//        List<Record> list;
//        Map<String, Boolean> map = new HashMap<String, Boolean>();
//        String sql = Db.getSql("core.getUserFuncMap");
//        list = Db.find(sql, userCode);
//        if (list != null) {
//            for (Record record : list) {
//                map.put(record.getStr("function_id"), true);
//            }
//        }
//
//        return map;
//    }
//
//    /**
//     * 保存用户列表数据
//     *
//     * @param tableList
//     * @param orgId
//     * @return
//     */
//    public boolean saveUserList(JSONArray userList, String orgId) {
//        if (userList == null)
//            return false;
//        for (int i = 0; i < userList.size(); i++) {
//            JSONObject obj = (JSONObject) userList.get(i);
//            String userCode = obj.getString("user_code");
//            SysUser user = new SysUser();
//            user.setId(userCode)
//                    .setUserCode(userCode)
//                    .setUserName(obj.getString("user_name"))
//                    .setOrgId(orgId)
//                    .setSex(Integer.parseInt(getObjectValue(obj.get("sex"))))
//                    .setEmail(obj.getString("email"))
//                    .setMobile(obj.getString("mobile"))
//                    .setTel(obj.getString("tel"))
//                    .setAllowLogin(Integer.parseInt(getObjectValue(obj.get("allow_login"))));
//            //存在用户则修改信息
//            if (isExist(userCode)) {
//                user.update();
//            } else {
//                user.setPasswd(Md5Kit.md5(userCode + "123"));
//                user.save();
//            }
//        }
//        return true;
//    }
//
//    public String getObjectValue(Object obj) {
//        if (obj instanceof JSONObject) {
//            try {
//                return ((JSONObject) obj).get("value").toString();
//            } catch (Exception e) {
//                e.printStackTrace();
//                System.out.println(obj);
//            }
//        }
//
//        return obj == null ? "" : obj.toString();
//    }


    /**
     * 修改皮肤颜色
     *
     * @param # userID
     * @param # theme
     * @return
     */
    public boolean updateTheme(String userId, String theme) {
        SysUser entity = (SysUser) findById(userId);
        entity.setTheme(theme);
        // 就是向用户个人设置了一个主题,因为每个人的主题不一样
        return entity.update();
    }

//    /**
//     * 获取选中的用户
//     *
//     * @param userIds  用户选择数据
//     * @param dataType 0：根据用户id查询,1:根据用户名称查询
//     * @return
//     */
//    public List<Record> getUserListByIds(String userIds) {
//        if (userIds != null) {
//            StringBuilder sb = new StringBuilder();
//            for (String id : userIds.split(",")) {
//                if (sb.length() > 0)
//                    sb.append(",");
//                sb.append("'").append(id).append("'");
//            }
//
//            if (sb.length() > 0) {
//                String sql = "select * from sys_user where id in(" + sb.toString() + ")";
//                return Db.find(sql);
//            }
//        }
//        return new ArrayList<Record>();
//    }
}
