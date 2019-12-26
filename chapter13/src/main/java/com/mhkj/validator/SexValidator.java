package com.mhkj.validator;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.temporal.ValueRange;
import java.util.Objects;

public class SexValidator extends ValidatorHandler<Integer> implements Validator<Integer> {

    @Override
    public boolean validate(ValidatorContext context, Integer t) {
        if (t < 0 || t > 2) {
            // 构建详细错误信息
            context.addError(ValidationError.create("sex值不正确").setErrorCode(100).setField("sex").setInvalidValue(t));
            // 简单处理，直接放入错误消息
            // context.addErrorMsg("sex值不正确");
            return false;
        }
        return true;
    }

}
