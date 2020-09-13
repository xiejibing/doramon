package com.xie.miaosha.exception;

import com.xie.miaosha.result.CodeMsg;
import com.xie.miaosha.result.Result;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * @author 14423
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public Result<String> exceptionHandler(HttpServletRequest request,  Exception e){
        e.printStackTrace();
        if (e instanceof BindException){
            BindException ex = (BindException) e;
            List<ObjectError> allErrors = ex.getAllErrors();
            ObjectError objectError = allErrors.get(0);
            String msg = objectError.getDefaultMessage();
            return Result.error(CodeMsg.BIND_ERROR.fillArgs(msg));
        }
        else if (e instanceof GlobalException){
            GlobalException ex = (GlobalException) e;
            return Result.error(ex.getCodeMsg());
        }
        else {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
    }
}
