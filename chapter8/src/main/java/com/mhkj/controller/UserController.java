package com.mhkj.controller;

import com.mhkj.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    /**
     * 不使用 BindingResult 接收校验结果，Spring 将抛出异常
     */
    @PostMapping("/add1")
    public User add1(@Valid @RequestBody User user) {
        return user;
    }

    /**
     * 使用 BindingResult 接收校验结果，自行组织输出内容
     */
    @PostMapping("/add2")
    public User add2(@Valid @RequestBody User user, BindingResult result) {
        if (result.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = result.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
                sb.append(",");
            }
            log.debug(sb.toString());
            return null;
        }
        return user;
    }

}
