package com.mhkj.consumer.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 发送方必须声明 Exchange、Queue及两者的关联关系 Routing
 * 接收方只需要声明 Queue 即可
 */
@Configuration
public class Config {

    @Bean
    public Queue TestDirectQueue() {
        return new Queue("TestDirectQueue",true);
    }

    @Bean
    public Queue fanoutQueueA() {
        return new Queue("FanoutQueueA",true);
    }

    @Bean
    public Queue fanoutQueueB() {
        return new Queue("FanoutQueueB",true);
    }

    @Bean
    public Queue topicQueueMan() {
        return new Queue("TopicQueue.man",true);
    }

    @Bean
    public Queue topicQueueWoman() {
        return new Queue("TopicQueue.woman",true);
    }

}
