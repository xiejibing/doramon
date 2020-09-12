package com.xie.miaosha.vo;

import com.xie.miaosha.validator.IsMobile;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
public class LoginVo {

    @NotNull
    @IsMobile
    private String mobile;
    @NotNull
    private String password;
}
