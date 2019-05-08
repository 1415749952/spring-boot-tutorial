package com.mhkj.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDto {

    private Long id;

    private String name;

    private LocalDate birthday;

    private Integer level;

    private Integer sex;

    private String password;

    private String salt;

}
