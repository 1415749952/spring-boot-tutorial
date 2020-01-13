package com.mhkj.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/info")
    public Object info() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /**
     * 单个角色：@Secured("ROLE_USER")
     * 多个角色任意一个：@Secured({"ROLE_USER","ROLE_ADMIN"})
     */
    @Secured("ROLE_USER")
    @GetMapping("/secure")
    public String secure() {
        return "Hello Security";
    }

    /**
     * 进入方法之前验证授权，支持表达式
     * 允许所有访问：@PreAuthorize("true")
     * 拒绝所有访问：@PreAuthorize("false")
     * 单个角色：@PreAuthorize("hasRole('ROLE_USER')")
     * 多个角色与条件：@PreAuthorize("hasRole('ROLE_USER') AND hasRole('ROLE_ADMIN')")
     * 多个角色或条件：@PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_ADMIN')")
     */
    @PreAuthorize("true")
    @GetMapping("/authorized")
    public String authorized() {
        return "Hello World";
    }

    @PreAuthorize("false")
    @GetMapping("/denied")
    public String denied() {
        return "Goodbye World";
    }

}
