package com.mhkj.controller;

import com.mhkj.entity.User;
import com.mhkj.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private UserRepository userRepository;

    @PostMapping("/add")
    public List<User> add(@RequestBody User user) {
        log.debug("用户新增");
        userRepository.save(user);
        return userRepository.findAll();
    }

    @PostMapping("/delete")
    public List<User> delete(Long id) {
        log.debug("用户删除");
        userRepository.deleteById(id);
        return userRepository.findAll();
    }

    @PostMapping("/list")
    public List<User> list() {
        return userRepository.findAll();
    }

    @PostMapping("/update")
    public List<User> update(User user) {
        log.debug("用户修改");
        userRepository.save(user);
        return userRepository.findAll();
    }

}
