package com.mhkj.sender.controller;

import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@RestController
public class SenderController {

    private RabbitTemplate rabbitTemplate;

    @PostMapping("/sendDirectMessage")
    public String sendDirectMessage(@RequestBody String msgData) {
        String msgId = String.valueOf(UUID.randomUUID());
        String sendTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String,Object> map = new HashMap<>(4);
        map.put("msgId", msgId);
        map.put("msgData", msgData);
        map.put("sendTime", sendTime);
        rabbitTemplate.convertAndSend("TestDirectExchange", "TestDirectRouting", map);
        return "ok";
    }

    @PostMapping("/sendFanoutMessage")
    public String sendFanoutMessage(@RequestBody String msgData) {
        String msgId = String.valueOf(UUID.randomUUID());
        String sendTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String,Object> map = new HashMap<>(4);
        map.put("msgId", msgId);
        map.put("msgData", msgData);
        map.put("sendTime", sendTime);
        rabbitTemplate.convertAndSend("TestFanoutExchange", null, map);
        return "ok";
    }

    @PostMapping("/sendTopicMessage")
    public String sendTopicMessage(@RequestBody String msgData) {
        String msgId = String.valueOf(UUID.randomUUID());
        String sendTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String,Object> map = new HashMap<>(4);
        map.put("msgId", msgId);
        map.put("msgData", msgData);
        map.put("sendTime", sendTime);
        rabbitTemplate.convertAndSend("TestFanoutExchange", "TopicQueue.woman", map);
        return "ok";
    }

}
