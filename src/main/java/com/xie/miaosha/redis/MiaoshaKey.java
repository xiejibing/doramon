package com.xie.miaosha.redis;

public class MiaoshaKey extends BasePrefix {
    private MiaoshaKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
    public static MiaoshaKey getMiaoshaPath = new MiaoshaKey(60,"miaoshaPath");
    public static MiaoshaKey getMiaoshaStartTime = new MiaoshaKey(0,"miaoshaStartTime");
    public static MiaoshaKey getMiaoshaVerifyCode = new MiaoshaKey(300,"verifyCode");

}
