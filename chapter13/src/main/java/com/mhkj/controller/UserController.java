package com.mhkj.controller;

import com.baidu.unbiz.fluentvalidator.annotation.FluentValid;
import com.mhkj.bo.UserBO;
import com.mhkj.utils.RestData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class UserController {

    @PostMapping("/register")
    public RestData<UserBO> register(@FluentValid @RequestBody UserBO userBo) {
        return new RestData<UserBO>().success("", userBo);
    }

}
