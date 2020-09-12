package com.xie.miaosha.exception;


import com.xie.miaosha.result.CodeMsg;

public class GlobalException extends RuntimeException {
    private CodeMsg codeMsg;

    public GlobalException(CodeMsg codeMsg){
        super(codeMsg.toString());
        this.codeMsg = codeMsg;
    }

    public CodeMsg getCodeMsg(){
        return this.codeMsg;
    }

}
