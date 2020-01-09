package com.mhkj.sender.service;

import com.mhkj.model.Email;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@AllArgsConstructor
@Service
public class MailService {

    private RabbitTemplate rabbitTemplate;

    public void send(String to, String subject, String content) {
        Email mail = new Email();
        mail.setTo(to);
        mail.setSubject(subject);
        mail.setContent(content);
        rabbitTemplate.convertAndSend("EmailExchange", "EmailRouting", mail);
    }

    public void send(String to, String subject, String content, LocalDateTime time) {
        Email mail = new Email();
        mail.setTo(to);
        mail.setSubject(subject);
        mail.setContent(content);
        rabbitTemplate.convertAndSend("EmailDelayExchange", "EmailDelayRouting", mail, msg -> {
            Duration duration = Duration.between(LocalDateTime.now(), time);
            msg.getMessageProperties().setExpiration(String.valueOf(duration.toMillis()));
            return msg;
        });
    }

}
