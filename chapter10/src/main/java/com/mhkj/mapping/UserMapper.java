package com.mhkj.mapping;

import com.mhkj.bo.UserBO;
import com.mhkj.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper MAPPER = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "username", target = "name")
    UserDto bo2Dto(UserBO bo);

}
