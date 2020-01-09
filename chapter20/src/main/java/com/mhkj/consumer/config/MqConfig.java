package com.mhkj.consumer.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MqConfig {

    @Bean
    public Queue emailQueue() {
        return new Queue("EmailQueue",true);
    }
}
