package com.cj.common.model;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;

/**
 * Generated by JFinal, do not modify this file.
 * <pre>
 * Example:
 * public void configPlugin(Plugins me) {
 *     ActiveRecordPlugin arp = new ActiveRecordPlugin(...);
 *     _MappingKit.mapping(arp);
 *     me.add(arp);
 * }
 * </pre>
 *
 * @author THINKPAD
 */
public class _MappingKit {

    public static void mapping(ActiveRecordPlugin arp) {
        arp.addMapping("data_dictionary", "id", DataDictionary.class);
        arp.addMapping("data_dictionary_value", "id", DataDictionaryValue.class);
        arp.addMapping("file_uploaded", "id", FileUploaded.class);
        arp.addMapping("form_sql", "id", FormSql.class);
        arp.addMapping("form_view", "id", FormView.class);
        arp.addMapping("session", "id", Session.class);
        arp.addMapping("sys_function", "id", SysFunction.class);
        arp.addMapping("sys_log", "id", SysLog.class);
        arp.addMapping("sys_org", "id", SysOrg.class);
        arp.addMapping("sys_role", "id", SysRole.class);
        arp.addMapping("sys_role_function", "id", SysRoleFunction.class);
        arp.addMapping("sys_tree", "id", SysTree.class);
        arp.addMapping("sys_user", "id", SysUser.class);
        arp.addMapping("sys_user_role", "id", SysUserRole.class);
        arp.addMapping("w_sys_tree", "id", WSysTree.class);
    }
}

