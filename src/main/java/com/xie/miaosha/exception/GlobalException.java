package com.xie.miaosha.exception;


import com.xie.miaosha.result.CodeMsg;

/**
 * @author 14423
 */
public class GlobalException extends RuntimeException {

    private CodeMsg codeMsg;

    public GlobalException(CodeMsg codeMsg){
        super(codeMsg.toString());
        this.codeMsg = codeMsg;
    }

    public void setCodeMsg(CodeMsg codeMsg) {
        this.codeMsg = codeMsg;
    }

    public CodeMsg getCodeMsg(){
        return this.codeMsg;
    }

}
