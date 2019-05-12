package com.mhkj.controller;

import com.mhkj.bo.UserBO;
import com.mhkj.entity.User;
import com.mhkj.mapping.UserMapper;
import com.mhkj.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
public class UserController {

    private UserRepository userRepository;

    @PostMapping("/register")
    public User register(@Valid @RequestBody UserBO userBO) {
        return userRepository.save(UserMapper.INSTANCE.bo2Do(userBO));
    }

}
