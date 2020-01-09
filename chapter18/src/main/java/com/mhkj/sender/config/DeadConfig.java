package com.mhkj.sender.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class DeadConfig {

    @Bean
    public Queue testDeadQueue() {
        Map<String, Object> params = new HashMap<>();
        params.put("x-dead-letter-exchange", "DeadExchange");
        params.put("x-dead-letter-routing-key", "DeadRouting");
        return new Queue("TestDeadQueue",true, false, false, params);
    }

    @Bean
    DirectExchange testDeadExchange() {
        return new DirectExchange("TestDeadExchange");
    }

    @Bean
    Binding bindingTestDeadQueue() {
        return BindingBuilder.bind(testDeadQueue()).to(testDeadExchange()).with("TestDeadRouting");
    }

    @Bean
    public Queue deadQueue() {
        return new Queue("DeadQueue",true);
    }

    @Bean
    DirectExchange deadExchange() {
        return new DirectExchange("DeadExchange");
    }

    @Bean
    Binding bindingDeadQueue() {
        return BindingBuilder.bind(deadQueue()).to(deadExchange()).with("DeadRouting");
    }

}
