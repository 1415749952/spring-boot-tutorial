package com.mhkj.controller;

import com.mhkj.BO.UserBO;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class UserController {

    @RequestMapping("/register")
    public String doRegister(@Valid @RequestBody UserBO bo) {
        return "success";
    }

}
