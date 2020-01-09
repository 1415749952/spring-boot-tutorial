package com.mhkj.test;

import com.mhkj.sender.SenderApplication;
import com.mhkj.sender.service.MailService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.LocalDateTime;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = SenderApplication.class)
public class MailTest {

    @Autowired
    private MailService mailService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Before
    public void init() {
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
    }

    @Test
    public void testSendEmail() throws Exception {
        mailService.send("gongm_24@126.com", "测试", "测试邮件");
    }

    @Test
    public void testSendDelayEmail() throws Exception {
        mailService.send("gongm_24@126.com", "测试延迟邮件", "测试邮件", LocalDateTime.now().plusMinutes(1));
    }

}
