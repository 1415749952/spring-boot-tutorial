package com.mhkj.controller;

import com.mhkj.bo.UserBO;
import com.mhkj.dto.UserDto;
import com.mhkj.mapping.UserMapper;
import com.mhkj.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
public class UserController {

    private UserService userService;

    @RequestMapping("/register")
    public UserDto doRegister(@Valid @RequestBody UserBO bo) {
        return userService.doSomething(UserMapper.MAPPER.bo2Dto(bo));
    }

}
