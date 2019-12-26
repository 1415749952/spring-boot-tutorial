package com.mhkj.mapping;

import com.mhkj.bo.PageBO;
import com.mhkj.bo.UserBO;
import com.mhkj.entity.User;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor"
)
public class UserMapperImpl implements UserMapper {

    @Override
    public User bo2Do(UserBO bo) {
        if ( bo == null ) {
            return null;
        }

        User user = new User();

        user.setName( bo.getMobile() );

        return user;
    }

    @Override
    public UserDto bo2Dto(UserBO bo) {
        if ( bo == null ) {
            return null;
        }

        UserDto userDto = new UserDto();

        userDto.setName( bo.getMobile() );

        return userDto;
    }

    @Override
    public PageBO copyPage(PageBO bo) {
        if ( bo == null ) {
            return null;
        }

        PageBO pageBO = new PageBO();

        pageBO.setPageNum( bo.getPageNum() );
        pageBO.setPageSize( bo.getPageSize() );

        return pageBO;
    }
}
