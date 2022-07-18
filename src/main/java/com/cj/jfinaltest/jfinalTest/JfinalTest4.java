package com.cj.jfinaltest.jfinalTest;

import com.cj.module.core.portal.core.service.SysUserService;

/**
 * @version 1.0
 * @author： jinmunan
 * @date： 2022-07-15 16:07
 */
public class JfinalTest4 {
    public static void main(String[] args) {
        new SysUserService().findByPk("user_code", "admin");
    }
}
