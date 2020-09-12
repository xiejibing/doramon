package com.xie.miaosha.result;

import com.xie.miaosha.domain.OrderInfo;
import lombok.Data;

@Data
public class CodeMsg {


    private int code;
    private String msg;
    //通用异常
    public static CodeMsg SUCCESS = new CodeMsg(0,"success");
    public static CodeMsg SERVER_ERROR = new CodeMsg(500100,"服务端异常");
    public static CodeMsg BIND_ERROR = new CodeMsg(500101, "参数校验异常：%s");
    public static CodeMsg REQUEST_ILLEGAL = new CodeMsg(500102, "请求不合法");
    public static CodeMsg REQUEST_TOO_FREQUENT = new CodeMsg(500103, "请求太频繁");


    //登录模块5002xx
    public static CodeMsg PASSWORD_EMPTY = new CodeMsg(500211,"密码不能为空");
    public static CodeMsg SESSION_ERROR = new CodeMsg(500216,"用户不存在或已经失效");
    public static CodeMsg MOBILE_EMPTY = new CodeMsg(500212,"手机号不能为空");
    public static CodeMsg MOBILE_ERROR = new CodeMsg(500213,"手机号格式错误");
    public static CodeMsg MOBILE_NOT_EXIST_ERROR = new CodeMsg(500214,"手机号不存在");
    public static CodeMsg PASSWORD_ERROR = new CodeMsg(500215,"密码错误");

    //商品模块5003xx
    //订单模块5004xx
    public static CodeMsg ORDER_NOT_EXISTS = new CodeMsg(500411,"订单不存在");

    //秒杀模块5005xx
    public static CodeMsg MIAOSHA_OVER = new CodeMsg(500500,"商品已经被抢完");
    public static CodeMsg MIAOSHA_REPEATED = new CodeMsg(500501,"不能重复秒杀");
    public static CodeMsg QUEUING = new CodeMsg(500502,"排队中");
    public static CodeMsg MIAOSHA_WAIT = new CodeMsg(500503,"请等待秒杀");
    public static CodeMsg MIAO_SHA_FAIL = new CodeMsg(500504,"秒杀失败");;

    private CodeMsg(int code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public CodeMsg fillArgs(Object... args){
       int code = this.code;
       String msg = String.format(this.msg,args);
       return new CodeMsg(code,msg);

    }

}
