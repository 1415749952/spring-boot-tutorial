package com.mhkj.controller;

import com.mhkj.bo.UserBO;
import com.mhkj.entity.User;
import com.mhkj.mapping.UserMapper;
import com.mhkj.repository.UserRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
@Api("用户管理")
public class UserController {

    private UserRepository userRepository;

    @ApiOperation(value = "用户注册", notes = "用户注册")
    @PostMapping("/register")
    public User register(@Valid @RequestBody @ApiParam(name = "注册对象", value = "JSON对象", required = true) UserBO userBO) {
        return userRepository.save(UserMapper.INSTANCE.bo2Do(userBO));
    }

}
