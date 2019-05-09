package com.mhkj.controller;

import com.mhkj.entity.User;
import com.mhkj.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/add")
    public List<User> add(@RequestBody User user) {
        userRepository.save(user);
        return userRepository.findAll();
    }

    @PostMapping("/delete")
    public List<User> delete(Long id) {
        userRepository.deleteById(id);
        return userRepository.findAll();
    }

    @PostMapping("/list")
    public List<User> list() {
        return userRepository.findAll();
    }

    @PostMapping("/update")
    public List<User> update(User user) {
        userRepository.save(user);
        return userRepository.findAll();
    }

}
