package com.mhkj.sender.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class DelayConfig {

    @Bean
    public Queue testDelayQueue() {
        Map<String, Object> params = new HashMap<>();
        params.put("x-dead-letter-exchange", "DeadExchange");
        params.put("x-dead-letter-routing-key", "DeadRouting");
        params.put("x-message-ttl", 5000);
        return new Queue("TestDelayQueue",true, false, false, params);
    }

    @Bean
    DirectExchange testDelayExchange() {
        return new DirectExchange("TestDelayExchange");
    }

    @Bean
    Binding bindingTestDelayQueue() {
        return BindingBuilder.bind(testDelayQueue()).to(testDelayExchange()).with("TestDelayRouting");
    }

//    @Bean
//    public Queue deadQueue() {
//        return new Queue("DeadQueue",true);
//    }
//
//    @Bean
//    DirectExchange deadExchange() {
//        return new DirectExchange("DeadExchange");
//    }
//
//    @Bean
//    Binding bindingDeadQueue() {
//        return BindingBuilder.bind(deadQueue()).to(deadExchange()).with("DeadRouting");
//    }

}
