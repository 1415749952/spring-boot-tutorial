package com.mhkj.controller;

import com.mhkj.BO.UserBO;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
public class UserController {

    /**
     * 如果校验失败，抛出异常
     */
    @RequestMapping("/register")
    public String doRegister(@RequestBody @Valid UserBO bo) {
        return "success";
    }

    /**
     * 如果校验失败，希望自行对结果进行处理，则使用 BindingResult 对象接收校验结果
     */
    @RequestMapping("/register1")
    public String doRegister1(@RequestBody @Valid UserBO bo, BindingResult result) {
        if (result.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = result.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
                sb.append(",");
            }
            return sb.toString();
        }
        return "success";
    }

}
