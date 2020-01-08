package com.mhkj.consumer.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * multiple：true表示确认deliveryTag下的所有消息，false表示只确认当前一个消息
 * requeue：true表示重新加入队列，而不会被转入死信队列，false表示不重新加入
 */
//@Component
//@RabbitListener(queues = "TestDirectQueue")
public class ConsumerDirectSimple {

    @RabbitHandler
    public void process(Map obj) throws IOException {
        System.out.println("DirectReceiver消费者收到消息  : " + obj.toString());
    }

}