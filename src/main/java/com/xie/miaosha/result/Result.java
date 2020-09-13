package com.xie.miaosha.result;

import lombok.Data;


public class Result<T> {
    private  int code;
    private String msg;
    private T data;


//    成功时调用
    public static<T> Result<T> success(T data){
        return new Result<T>(data);
    }
    //失败
    public static <T> Result<T> error(CodeMsg codeMsg){
        return new Result<T>(codeMsg);
    }

    private Result(T data){
        this.code = 0;
        this.msg = "success";
        this.data = data;
    }

    private Result(CodeMsg codeMsg){
        if (codeMsg == null) {
            return;
        }
        this.code = codeMsg.getCode();
        this.msg = codeMsg.getMsg();
    }



}
