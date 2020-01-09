package com.mhkj;

import com.mhkj.sender.SenderApplication;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = SenderApplication.class)
public class MqTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testDeadQueue() throws Exception {
        String msgId = String.valueOf(UUID.randomUUID());
        String sendTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String,Object> map = new HashMap<>(4);
        map.put("msgId", msgId);
        map.put("msgData", "test dead letter queue");
        map.put("sendTime", sendTime);
        rabbitTemplate.convertAndSend("TestDeadExchange", "TestDeadRouting", map);
    }
    @Test
    public void testDelayQueue() throws Exception {
        String msgId = String.valueOf(UUID.randomUUID());
        String sendTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String,Object> map = new HashMap<>(4);
        map.put("msgId", msgId);
        map.put("msgData", "test delay queue");
        map.put("sendTime", sendTime);
        rabbitTemplate.convertAndSend("TestDelayExchange", "TestDelayRouting", map);
    }

    @Test
    public void testDelayQueueWithMessage() throws Exception {
        String msgId = String.valueOf(UUID.randomUUID());
        String sendTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String,Object> map = new HashMap<>(4);
        map.put("msgId", msgId);
        map.put("msgData", "test dead letter queue");
        map.put("sendTime", sendTime);
        rabbitTemplate.convertAndSend("TestDeadExchange", "TestDeadRouting", map, msg -> {
            msg.getMessageProperties().setExpiration("5000");
            return msg;
        });
    }

}
