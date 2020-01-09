package com.mhkj.sender.config;

import org.springframework.amqp.core.*;
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

    @Bean
    DirectExchange emailExchange() {
        return new DirectExchange("EmailExchange");
    }

    @Bean
    Binding bindingEmailQueue() {
        return BindingBuilder.bind(emailQueue()).to(emailExchange()).with("EmailRouting");
    }

    @Bean
    public Queue emailDelayQueue() {
        Map<String, Object> params = new HashMap<>(2);
        params.put("x-dead-letter-exchange", "EmailExchange");
        params.put("x-dead-letter-routing-key", "EmailRouting");
        return new Queue("EmailDelayQueue",true, false, false, params);
    }

    @Bean
    DirectExchange emailDelayExchange() {
        return new DirectExchange("EmailDelayExchange");
    }

    @Bean
    Binding bindingEmailDelayQueue() {
        return BindingBuilder.bind(emailDelayQueue()).to(emailDelayExchange()).with("EmailDelayRouting");
    }

}
