package com.mhkj.controller;

import com.mhkj.entity.User;
import com.mhkj.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private UserRepository userRepository;

    /**
     * 不使用 BindingResult 接收校验结果，Spring 将抛出异常
     */
    @PostMapping("/add")
    public List<User> add(@Valid @RequestBody User user) {
        userRepository.save(user);
        return userRepository.findAll();
    }

}
