package com.xie.miaosha.vo;

import com.xie.miaosha.validator.IsMobile;

import javax.validation.constraints.NotNull;

public class LoginVo {

    @NotNull
    @IsMobile
    private String mobile;
    @NotNull
    private String password;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
