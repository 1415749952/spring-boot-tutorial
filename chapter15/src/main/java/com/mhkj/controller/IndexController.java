package com.mhkj.controller;

import com.mhkj.annotation.SysLog;
import com.mhkj.bo.UserBO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @SysLog("用户注册")
    @PostMapping("/register")
    public UserBO register(@RequestBody UserBO userBo) {
        return userBo;
    }

}
