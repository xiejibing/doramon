package com.xie.miaosha.redis;

public class OrderKey extends BasePrefix {
    public static KeyPrefix getByUidAndGid = new OrderKey(0,"moug");

    public OrderKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
}
