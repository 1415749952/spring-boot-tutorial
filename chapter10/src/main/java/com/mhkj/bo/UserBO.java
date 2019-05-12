package com.mhkj.bo;

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
     * 手机号，长度在6-16个字符之间，必须参数
     */
    @NotBlank(message = "手机号不能为空")
    private String mobile;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Length(min = 6, max = 16, message = "密码长度必须在6-16位之间")
    private String upwd;

}
