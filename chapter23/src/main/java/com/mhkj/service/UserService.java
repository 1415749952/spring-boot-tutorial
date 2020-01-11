package com.mhkj.service;

import com.mhkj.entity.User;
import com.mhkj.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.stereotype.Service;

@CacheConfig(cacheNames = "user")
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Cacheable(key = "#id")
    public User getUser(Long id) {
        return userRepository.getOne(id);
    }

    @CachePut(key = "#user.id")
    public User addUser(User user) {
        userRepository.save(user);
        return user;
    }

    @CachePut(key = "#user.id")
    public User updateUser(User user) {
        userRepository.save(user);
        return user;
    }

    @CacheEvict(key = "#id")
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

}