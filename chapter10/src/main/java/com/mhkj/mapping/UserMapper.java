package com.mhkj.mapping;

import com.mhkj.bo.UserBO;
import com.mhkj.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "mobile", target = "name")
    User bo2Do(UserBO bo);

}
