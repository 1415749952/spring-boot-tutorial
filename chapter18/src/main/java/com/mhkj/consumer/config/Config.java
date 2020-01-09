package com.mhkj.consumer.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 发送方必须声明 Exchange、Queue及两者的关联关系 Routing
 * 接收方只需要声明 Queue 即可
 */
@Configuration
public class Config {

    @Bean
    public Queue testDirectQueue() {
        Map<String, Object> params = new HashMap<>();
        params.put("x-dead-letter-exchange", "DeadExchange");
        params.put("x-dead-letter-routing-key", "DeadRouting");
        return new Queue("TestDeadQueue",true, false, false, params);
    }

    @Bean
    public Queue TestDeadQueue() {
        return new Queue("DeadQueue",true);
    }

}
