package com.mhkj.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    /**
     * 单个角色：@Secured("ROLE_USER")
     * 多个角色任意一个：@Secured({"ROLE_USER","ROLE_ADMIN"})
     */
    @Secured("ROLE_USER")
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
    public String authorized() {
        return "Hello World";
    }

    @PreAuthorize("false")
    public String denied() {
        return "Goodbye World";
    }

    /**
     * JSR-250 注解
     * 允许所有访问：@PermitAll
     * 拒绝所有访问：@DenyAll
     * 多个角色任意一个：@RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
     */
    @PermitAll
    public String jsrAuthorized() {
        return "Goodbye World";
    }

    @DenyAll
    public String jsrDenied() {
        return "Goodbye World";
    }

}
