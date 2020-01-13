package com.mhkj.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mhkj.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRoleRepository extends BaseMapper<UserRole> {
}
