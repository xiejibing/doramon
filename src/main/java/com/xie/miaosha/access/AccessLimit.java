package com.xie.miaosha.access;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessLimit {
    int seconds();
    int maxCount();//多少时间内最多访问多少次
    boolean needLogin() default true;
}
