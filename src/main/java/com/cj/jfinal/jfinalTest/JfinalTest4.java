package com.cj.jfinal.jfinalTest;

import com.cj.common.module.portal.core.service.SysUserService;
import com.cj.common.util.kit.RSAKit;

import java.io.InputStream;

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
