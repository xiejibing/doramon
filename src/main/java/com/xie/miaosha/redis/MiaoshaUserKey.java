package com.xie.miaosha.redis;

public class MiaoshaUserKey extends BasePrefix {

    private static int TOKEN_EXPIRE = 3600*24*2;//token生命周期
    public MiaoshaUserKey(int expireSeconds, String prefix) {
        super(TOKEN_EXPIRE,prefix);
    }
    public static MiaoshaUserKey getByToken = new MiaoshaUserKey(TOKEN_EXPIRE,"tk");
    public static MiaoshaUserKey getById = new MiaoshaUserKey(0,"id");

}
