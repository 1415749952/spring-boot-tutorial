package com.mhkj.sender.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 发送方必须声明 Exchange、Queue及两者的关联关系 Routing
 * 接收方只需要声明 Queue 即可
 */
@Configuration
public class DirectConfig {

    @Bean
    public Queue testDirectQueue() {
        return new Queue("TestDirectQueue",true);
    }

    @Bean
    DirectExchange testDirectExchange() {
        return new DirectExchange("TestDirectExchange");
    }

    @Bean
    Binding bindingDirect() {
        return BindingBuilder.bind(testDirectQueue()).to(testDirectExchange()).with("TestDirectRouting");
    }

}
