package com.mhkj.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {

    private Long id;

    private String name;

    private Integer sex;

    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private LocalDate birthday;

    private Integer level;

}
