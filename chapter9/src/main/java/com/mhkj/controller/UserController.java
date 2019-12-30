package com.mhkj.controller;

import com.mhkj.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    /**
     * 不使用 BindingResult 接收校验结果，Spring 将抛出异常
     */
    @PostMapping("/add")
    public User add(@Valid @RequestBody User user) {
        return user;
    }

}
