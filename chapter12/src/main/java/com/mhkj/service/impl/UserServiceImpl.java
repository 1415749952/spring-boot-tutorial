package com.mhkj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mhkj.entity.User;
import com.mhkj.repository.UserRepository;
import com.mhkj.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserRepository, User> implements UserService {
}
