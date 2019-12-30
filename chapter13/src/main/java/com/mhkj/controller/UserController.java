package com.mhkj.controller;

import com.baidu.unbiz.fluentvalidator.annotation.FluentValid;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mhkj.bo.PageBO;
import com.mhkj.bo.UserBO;
import com.mhkj.entity.User;
import com.mhkj.mapping.UserMapper;
import com.mhkj.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
@Api("用户管理")
public class UserController {

    private UserService userService;

    @ApiOperation(value = "用户注册", notes = "用户注册")
    @PostMapping("/register")
    public User register(@FluentValid @RequestBody UserBO userBo) {
        User user = UserMapper.INSTANCE.bo2Do(userBo);
        userService.save(user);
        return user;
    }

    @ApiOperation(value = "用户删除", notes = "用户删除")
    @PostMapping("/delete")
    public List<User> delete(Long id) {
        userService.removeById(id);
        return userService.list();
    }

    @ApiOperation(value = "获取指定用户信息", notes = "获取指定用户信息")
    @PostMapping("/get")
    public User get(Long id) {
        return userService.getById(id);
    }

    @ApiOperation(value = "修改用户信息", notes = "修改用户信息")
    @PostMapping("/update")
    public List<User> update(User user) {
        userService.updateById(user);
        return userService.list();
    }

    @ApiOperation(value = "分页查询所有用户信息", notes = "分页查询所有用户信息")
    @PostMapping("/search")
    public IPage<User> search(PageBO bo) {
        return userService.page(new Page<>(bo.getPageNum(), bo.getPageSize()));
    }

}