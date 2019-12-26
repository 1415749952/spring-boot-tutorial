package com.mhkj.mapping;

import com.mhkj.bo.PageBO;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDto {

    private Long id;

    private String name;

    private Integer sex;

    private LocalDate birthday;

    private Integer level;

    private PageBO page;

}
