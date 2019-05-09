package com.mhkj.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HelloController {

    @PostMapping("/hello")
    public Map<String, Object> hello(@RequestBody Map<String, Object> map) {
        return map;
    }

}
