package com.mhkj.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

/**
 * 自定义校验
 * 实现 ConstraintValidator 接口
 * 通过 ValueRange 注解自定义可输入值，如果入参在自定义范围内，则接收，否则提示错误
 */
public class ValueRangeValidator implements ConstraintValidator<ValueRange, Object> {

    private String[] values;

    @Override
    public void initialize(ValueRange constraintAnnotation) {
        values = constraintAnnotation.values();
    }

    /**
     * 校验函数
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        String v = value.toString();
        for (String s : values) {
            if (Objects.equals(s, v)) {
                return true;
            }
        }
        return false;
    }

}
