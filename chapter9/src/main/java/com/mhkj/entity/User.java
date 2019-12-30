package com.mhkj.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mhkj.validator.ValueRange;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class User {

    private Long id;

    /**
     * 用户名，长度在6-16个字符之间，必须参数
     */
    @NotBlank(message = "用户名不能为空")
    @Length(min = 6, max = 16, message = "用户名长度必须在6-16个字符之间")
    private String name;

    /**
     * 性别，使用字典值，0:保密 | 1:男 | 2:女
     */
    @ValueRange(values = {"0", "1", "2"}, message = "性别值不正确")
    private Integer sex;

    /**
     * 出生日期，格式为 yyyy-MM-dd，必须为过去的日期，不必须参数
     */
    @Past(message = "出生日期必须早于当前日期")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private LocalDate birthday;

    /**
     * 等级，整数，0-5之间，必须参数
     */
    @NotNull(message = "用户等级不能为空")
    @Min(value = 0, message = "用户等级最小为0")
    @Max(value = 5, message = "用户等级最大为5")
    @Digits(integer = 1, fraction = 0, message = "用户等级必须为整数")
    private Integer level;

}
