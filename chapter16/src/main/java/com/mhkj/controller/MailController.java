package com.mhkj.controller;

import com.mhkj.model.Mail;
import com.mhkj.service.MailService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
public class MailController {

    private MailService mailService;

    @PostMapping("/sendText")
    public String sendText(@RequestBody Mail mail) {
        try {
            mailService.send(mail);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "OK";
    }

    @PostMapping("/sendHtml")
    public String sendHtml(@RequestBody Mail mail) {
        try {
            mailService.sendHtml(mail);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "OK";
    }

    @PostMapping("/sendFreemarkerTpl")
    public String sendFreemarkerTpl(@RequestBody Mail mail) {
        try {
            mailService.sendFreemarker(mail);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "OK";
    }

    @PostMapping("/sendFreemarkerText")
    public String sendFreemarkerText(@RequestBody Mail mail) {
        try {
            mailService.sendStringFreemarker(mail);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "OK";
    }

    @PostMapping("/sendThymeleaf")
    public String sendThymeleaf(@RequestBody Mail mail) {
        try {
            mailService.sendThymeleaf(mail);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "OK";
    }

}
