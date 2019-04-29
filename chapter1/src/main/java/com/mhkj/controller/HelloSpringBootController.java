package com.mhkj.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloSpringBootController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello SpringBoot";
    }

}
