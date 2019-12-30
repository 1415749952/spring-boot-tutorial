package com.mhkj.controller;

import com.mhkj.bo.UserBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@Api("用户管理")
public class UserController {

    @ApiOperation(value = "用户注册", notes = "用户注册")
    @PostMapping("/register")
    public UserBO register(@RequestBody @ApiParam(name = "注册对象", value = "JSON对象", required = true) UserBO userBO) {
        return userBO;
    }

}
