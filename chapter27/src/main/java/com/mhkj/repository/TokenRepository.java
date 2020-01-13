package com.mhkj.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mhkj.entity.Token;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TokenRepository extends BaseMapper<Token> {
}
