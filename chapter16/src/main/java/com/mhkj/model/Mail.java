package com.mhkj.model;

import lombok.Data;

import java.util.Map;

@Data
public class Mail {

    // 接收方邮件
    private String email;
    // 主题
    private String subject;
    // 邮件内容
    private String content;
    // 模板
    private String template;
    // 入参
    private Map<String, String> arguments;

}
