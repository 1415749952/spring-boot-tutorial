package com.mhkj.sender.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TopicConfig {

    @Bean
    public Queue topicQueueMan() {
        return new Queue("TopicQueue.man",true);
    }

    @Bean
    public Queue topicQueueWoman() {
        return new Queue("TopicQueue.woman",true);
    }

    @Bean
    TopicExchange topicExchange() {
        return new TopicExchange("TestTopicExchange");
    }

    @Bean
    Binding bindingQueueMan() {
        return BindingBuilder.bind(topicQueueMan()).to(topicExchange()).with("TopicQueue.man");
    }

    @Bean
    Binding bindingQueueWoman() {
        return BindingBuilder.bind(topicQueueWoman()).to(topicExchange()).with("TopicQueue.#");
    }

}
