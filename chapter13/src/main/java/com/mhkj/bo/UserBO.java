package com.mhkj.bo;

import com.baidu.unbiz.fluentvalidator.annotation.FluentValidate;
import com.mhkj.validator.SexValidator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ApiModel("用户注册对象")
public class UserBO {

    @ApiModelProperty(value = "手机号", required = true)
    @NotBlank(message = "手机号不能为空")
    private String mobile;

    @ApiModelProperty(value = "密码", required = true)
    @NotBlank(message = "密码不能为空")
    @Length(min = 6, max = 16, message = "密码长度必须在6-16位之间")
    private String upwd;

    @ApiModelProperty(value = "性别")
    @FluentValidate({SexValidator.class})
    private Integer sex;

}
