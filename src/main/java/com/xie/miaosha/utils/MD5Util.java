package com.xie.miaosha.utils;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {

    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }

    private static final String salt = "1a2b3c";

    /**
     * 输入密码加密
     * @param inputPass
     * @return
     */
    public static String inputToFormPass(String inputPass){
        String str = salt.charAt(0)+salt.charAt(2)+inputPass+salt.charAt(5)+salt.charAt(4);
        return md5(str);
    }

    /**
     * formPass--->dbPass加密
     * @param formPass
     * @param salt
     * @return
     */
    public static String formPassToDBPass(String formPass, String salt){
        String str = salt.charAt(0)+salt.charAt(2)+formPass+salt.charAt(5)+salt.charAt(4);
        return md5(str);
    }

    public static String inputPassToDBPass(String inputPass, String DBsalt){
       String formPass =  inputToFormPass(inputPass);
       String dbPass = formPassToDBPass(formPass,DBsalt);
       return dbPass;
    }

    public static void main(String[] args) {
        System.out.println(formPassToDBPass("d3b1294a61a07da9b49b6e22b2cbd7f9",salt));

    }

}
