package com.xie.miaosha.redis;

public class AccessKey extends BasePrefix {

    public AccessKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static AccessKey getAccessCount(int expireSeconds){
        return new AccessKey(expireSeconds,"ak");
    }


}
