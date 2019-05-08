package com.mhkj.entity;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class User {

    private Long id;
    private String name;
    private Integer sex;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

}
