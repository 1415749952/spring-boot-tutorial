package com.mhkj.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mhkj.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRepository extends BaseMapper<User> {
}
