package com.xie.miaosha.utils;

import java.util.UUID;

/**
 * @author 14423
 */
public class UUIDUtil {

    public static String uuid(){
        return UUID.randomUUID().toString().replace("-","");
    }
}
