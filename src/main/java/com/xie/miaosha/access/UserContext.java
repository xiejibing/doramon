package com.xie.miaosha.access;

import com.xie.miaosha.domain.MiaoshaUser;

public class UserContext  {

    private static ThreadLocal<MiaoshaUser>  userThreadLocal = new ThreadLocal<MiaoshaUser>();
    public static void setUser(MiaoshaUser user){
        userThreadLocal.set(user);
    }

    public MiaoshaUser getUser(){
        return userThreadLocal.get();
    }
}
