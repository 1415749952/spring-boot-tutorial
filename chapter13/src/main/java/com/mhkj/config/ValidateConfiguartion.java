package com.mhkj.config;

import com.baidu.unbiz.fluentvalidator.DefaultValidateCallback;
import com.baidu.unbiz.fluentvalidator.ValidateCallback;
import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.exception.RuntimeValidateException;
import com.baidu.unbiz.fluentvalidator.interceptor.FluentValidateInterceptor;
import com.baidu.unbiz.fluentvalidator.validator.element.ValidatorElementList;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

@Configuration
public class ValidateConfiguartion {

    @Bean
    public FluentValidateInterceptor fluentValidateInterceptor() {
        FluentValidateInterceptor fluentValidateInterceptor = new FluentValidateInterceptor();
        fluentValidateInterceptor.setCallback(validateCallback());
        return fluentValidateInterceptor;
    }

    @Bean
    public ValidateCallback validateCallback() {
        return new HussarValidateCallBack();
    }

    @Bean
    public BeanNameAutoProxyCreator beanNameAutoProxyCreator(){
        BeanNameAutoProxyCreator proxyCreator = new BeanNameAutoProxyCreator();
        proxyCreator.setBeanNames("*Controller");
        proxyCreator.setInterceptorNames("fluentValidateInterceptor");
        return proxyCreator;
    }

    public static class HussarValidateCallBack extends DefaultValidateCallback implements ValidateCallback {
        @Override
        public void onSuccess(ValidatorElementList validatorElementList) {
            super.onSuccess(validatorElementList);
        }

        @Override
        public void onFail(ValidatorElementList validatorElementList, List<ValidationError> errors) {
            throw new RuntimeException(errors.get(0).getErrorMsg());
        }

        @Override
        public void onUncaughtException(Validator validator, Exception e, Object target) throws Exception {
            throw new RuntimeException(e);
        }
    }

}
