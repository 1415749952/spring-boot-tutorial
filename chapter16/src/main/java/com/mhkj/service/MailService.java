package com.mhkj.service;

import com.mhkj.model.Mail;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.ResourceUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Map;

@AllArgsConstructor
@Service
public class MailService {

    private MailProperties properties;
    private JavaMailSender mailSender;
    private Configuration configuration;
    private TemplateEngine templateEngine;

    public void send(Mail mail) throws Exception {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(properties.getUsername());
        message.setTo(mail.getEmail());
        message.setSubject(mail.getSubject());
        message.setText(mail.getContent());
        mailSender.send(message);
    }

    public void sendHtml(Mail mail) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(properties.getUsername());
        helper.setTo(mail.getEmail());
        helper.setSubject(mail.getSubject());
        helper.setText(
                "<html><body><img src=\"cid:test\" ></body></html>",
                true);
        // 发送图片
        File file = ResourceUtils.getFile("classpath:static/image/test.png");
        helper.addInline("test", file);
        // 发送附件
        file = ResourceUtils.getFile("classpath:static/image/test.txt");
        helper.addAttachment("测试", file);
        mailSender.send(message);
    }

    public void sendFreemarker(Mail mail) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        //这里可以自定义发信名称比如：爪哇笔记
        helper.setFrom(properties.getUsername());
        helper.setTo(mail.getEmail());
        helper.setSubject(mail.getSubject());
        Template template = configuration.getTemplate(mail.getTemplate());
        String text = FreeMarkerTemplateUtils.processTemplateIntoString(
                template, mail.getArguments());
        helper.setText(text, true);
        mailSender.send(message);
    }

    public void sendStringFreemarker(Mail mail) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(properties.getUsername());
        helper.setTo(mail.getEmail());
        helper.setSubject(mail.getSubject());
        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        stringTemplateLoader.putTemplate(mail.getTemplate(), mail.getContent());
        configuration.setTemplateLoader(stringTemplateLoader);
        Template template = configuration.getTemplate(mail.getTemplate());
        String text = FreeMarkerTemplateUtils.processTemplateIntoString(
                template, mail.getArguments());
        helper.setText(text, true);
        mailSender.send(message);
    }

    public void sendThymeleaf(Mail mail) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(properties.getUsername());
        helper.setTo(mail.getEmail());
        helper.setSubject(mail.getSubject());
        Context context = new Context();
        for (Map.Entry<String, String> entry : mail.getArguments().entrySet()) {
            context.setVariable(entry.getKey(), entry.getValue());
        }
        String text = templateEngine.process(mail.getTemplate(), context);
        helper.setText(text, true);
        mailSender.send(message);
    }

}
