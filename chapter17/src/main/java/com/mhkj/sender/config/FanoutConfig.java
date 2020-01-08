package com.mhkj.sender.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 发送方必须声明 Exchange、Queue及两者的关联关系 Routing
 * 接收方只需要声明 Queue 即可
 */
@Configuration
public class FanoutConfig {

    @Bean
    public Queue fanoutQueueA() {
        return new Queue("FanoutQueueA",true);
    }

    @Bean
    public Queue fanoutQueueB() {
        return new Queue("FanoutQueueB",true);
    }

    @Bean
    FanoutExchange fanoutExchange() {
        return new FanoutExchange("TestFanoutExchange");
    }

    @Bean
    Binding bindingQueueA() {
        return BindingBuilder.bind(fanoutQueueA()).to(fanoutExchange());
    }

    @Bean
    Binding bindingQueueB() {
        return BindingBuilder.bind(fanoutQueueB()).to(fanoutExchange());
    }

}
