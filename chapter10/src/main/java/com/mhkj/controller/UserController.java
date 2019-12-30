package com.mhkj.controller;

import com.mhkj.bo.UserBO;
import com.mhkj.entity.User;
import com.mhkj.mapping.UserMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Slf4j
@AllArgsConstructor
public class UserController {

    @PostMapping("/register")
    public User register(@RequestBody UserBO userBo) {
        User user = UserMapper.INSTANCE.bo2Do(userBo);
        // ... 执行数据库操作
        return user;
    }

}
