package com.cj.jfinal.jfinalTest;

import com.cj.common.util.kit.RSAKit;

import java.io.InputStream;

/**
 * @version 1.0
 * @author： jinmunan
 * @date： 2022-07-15 16:07
 */
public class JfinalTest3 {
    public static void main(String[] args) {

        InputStream is = RSAKit.class.getClassLoader().getResourceAsStream("RSAKey.txt");
        System.out.println(is);
    }
}
