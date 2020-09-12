package com.xie.miaosha.utils;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidatorUtils {
    private static final Pattern pattern = Pattern.compile("^1[0-9]{10}$");
    public static boolean isMobile(String mobile){
        if (StringUtils.isEmpty(mobile)){
            return false;
        }
        Matcher matcher = pattern.matcher(mobile);
        return matcher.matches();
    }

}
