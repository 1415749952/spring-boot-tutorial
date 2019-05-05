package com.mhkj.BO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mhkj.validator.ValueRange;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Getter
@Setter
public class UserBO {

    /**
     * 用户名，长度在6-16个字符之间，必须参数
     */
    @NotBlank(message = "用户名不能为空")
    @Length(min = 6, max = 16, message = "用户名长度必须在6-16个字符之间")
    private String username;

    /**
     * 出生日期，格式为 yyyy-MM-dd，必须为过去的日期，不必须参数
     */
    @Past(message = "出生日期必须早于当前日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    /**
     * 等级，整数，0-5之间，必须参数
     */
    @NotNull(message = "用户等级不能为空")
    @Min(value = 0, message = "用户等级最小为0")
    @Max(value = 5, message = "用户等级最大为5")
    @Digits(integer = 1, fraction = 0, message = "用户等级必须为整数")
    private Integer level;

    @ValueRange(values = {"0", "1", "2"})
    private Integer sex;

}
