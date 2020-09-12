package com.xie.miaosha.controller;

import com.xie.miaosha.domain.MiaoshaUser;
import com.xie.miaosha.domain.User;
import com.xie.miaosha.rabbitmq.MQSender;
import com.xie.miaosha.redis.RedisService;
import com.xie.miaosha.redis.UserKey;
import com.xie.miaosha.result.CodeMsg;
import com.xie.miaosha.result.Result;
import com.xie.miaosha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/user")
public class TestController {
    @Autowired
    UserService userService;
    @Autowired
    RedisService redisService;
    @Autowired
    MQSender sender;

    @RequestMapping("/info")
    @ResponseBody
    public Result<MiaoshaUser> test(Model model, MiaoshaUser user){
        return Result.success(user);
    }
    @RequestMapping("/helloSuccess")
    @ResponseBody
    public Result<String> success(){
        return Result.success("hello success");
    }

    @RequestMapping("/helloError")
    @ResponseBody
    public Result<String> error(){
        return Result.error(CodeMsg.SERVER_ERROR);
    }

    @RequestMapping("/getUser")
    @ResponseBody
    public Result<User> getUser(){
        return Result.success(userService.getUserById(1));
    }

    @RequestMapping("/set")
    @ResponseBody
    public Result<Boolean> set(){
        User user = new User();
        user.setId(1);
        user.setName("xiejibing");
        redisService.set(UserKey.getById,""+1,user);
        return Result.success(true);
    }

    @RequestMapping("/get")
    @ResponseBody
    public Result<User> get(){
        User user = redisService.get(UserKey.getById,""+1,User.class);
        return Result.success(user);
    }

    @RequestMapping("/mq")
    @ResponseBody
    public Result<String> mq(){
        sender.send("hello mq ,i am sender");
        return Result.success("hello mq");
    }
    @RequestMapping("/mqTopic")
    @ResponseBody
    public Result<String> mqTopic(){
        sender.sendTopic("hello mq, I am sender");
        return Result.success("hello mq");
    }

    @RequestMapping("/mqFanout")
    @ResponseBody
    public Result<String> mqFanout(){
        sender.sendFanout("hello mq, I am sender");
        return Result.success("hello mq");
    }
    @RequestMapping("/mqHeaders")
    @ResponseBody
    public Result<String> mqHeaders(){
        sender.sendHeaders("hello mq, I am sender");
        return Result.success("hello mq");
    }
}
