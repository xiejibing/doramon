package com.xie.miaosha.controller;

import com.sun.deploy.net.HttpResponse;
import com.xie.miaosha.result.CodeMsg;
import com.xie.miaosha.result.Result;
import com.xie.miaosha.service.MiaoshaUserService;
import com.xie.miaosha.service.UserService;
import com.xie.miaosha.utils.ValidatorUtils;
import com.xie.miaosha.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    MiaoshaUserService userService;

    private static Logger log = LoggerFactory.getLogger(LoginController.class);

    @RequestMapping("/to_login")
    public String toLogin() {
        return "login";
    }

    @RequestMapping("/do_login")
    @ResponseBody
    public Result<String> doLogin(@Valid LoginVo loginVo, HttpServletResponse response) {
        //log.info(loginVo.toString());
        //使用@IsMobile注解和@NotNUll来完成
//        //参数校验
//        if (StringUtils.isEmpty(passInput)){//密码为空
//           return Result.error(CodeMsg.PASSWORD_EMPTY);
//        }
//        if (StringUtils.isEmpty(mobile)){//手机号码为空
//            return Result.error(CodeMsg.MOBILE_EMPTY);
//        }
//
//        if (!ValidatorUtils.isMobile(mobile)){//手机号校验
//            return Result.error(CodeMsg.MOBILE_ERROR);
//        }
        //登录
        String token = userService.login(loginVo, response);
        return Result.success(token);
    }

}
