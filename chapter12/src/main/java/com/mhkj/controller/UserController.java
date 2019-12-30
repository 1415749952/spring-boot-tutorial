package com.mhkj.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mhkj.bo.PageBO;
import com.mhkj.entity.User;
import com.mhkj.service.UserService;
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
public class UserController {

    private UserService userService;

    @PostMapping("/register")
    public User register(@Valid @RequestBody User user) {
        userService.save(user);
        return user;
    }

    @PostMapping("/delete")
    public List<User> delete(Long id) {
        userService.removeById(id);
        return userService.list();
    }

    @PostMapping("/list")
    public List<User> list() {
        return userService.list();
    }

    @PostMapping("/update")
    public List<User> update(User user) {
        userService.updateById(user);
        return userService.list();
    }

    @PostMapping("/search")
    public IPage<User> search(PageBO bo) {
        return userService.page(new Page<>(bo.getPageNum(), bo.getPageSize()));
    }

}
