package com.mhkj.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mhkj.entity.Role;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RoleRepository extends BaseMapper<Role> {
}
