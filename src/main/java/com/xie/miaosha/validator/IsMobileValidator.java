package com.xie.miaosha.validator;

import com.xie.miaosha.utils.ValidatorUtils;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {
    private boolean required;
    @Override
    public void initialize(IsMobile constraintAnnotation) {
        required = constraintAnnotation.required();
    }
    /**
     * @param value 要校验的值
     * @param context
     * @return
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (required){//这个值是必须的
            return ValidatorUtils.isMobile(value);
        }else {//这个值不是必须的
            if (StringUtils.isEmpty(value)){//空的话直接返回true
                return true;
            }
            else {//参数校验
                return ValidatorUtils.isMobile(value);
            }
        }
    }
}
