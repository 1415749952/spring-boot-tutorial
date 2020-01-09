package com.mhkj.consumer.consumer;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * multiple：true表示确认deliveryTag下的所有消息，false表示只确认当前一个消息
 * requeue：true表示重新加入队列，而不会被转入死信队列，false表示不重新加入
 */
@Configuration
public class ConsumerWithAck {

    @Component
    @RabbitListener(queues = "TestDirectQueue")
    public class ConsumerDirectQueue {
        @RabbitHandler
        public void process(Map obj, Channel channel, Message message) throws IOException {
            try {
//                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                System.out.println("DirectQueue消费者收到消息并NACK返回  : " + obj.toString());
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
            } catch (Exception e) {
                e.printStackTrace();
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
            }
        }
    }

    @Component
    @RabbitListener(queues = "FanoutQueueA")
    public class ConsumerFanoutQueueA {
        @RabbitHandler
        public void process(Map obj, Channel channel, Message message) throws IOException {
            try {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                System.out.println("FanoutQueueA消费者收到消息并ACK返回  : " + obj.toString());
            } catch (Exception e) {
                e.printStackTrace();
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            }
        }
    }

    @Component
    @RabbitListener(queues = "FanoutQueueB")
    public class ConsumerFanoutQueueB {
        @RabbitHandler
        public void process(Map obj, Channel channel, Message message) throws IOException {
            try {
                System.out.println("FanoutQueueB消费者收到消息并ACK返回  : " + obj.toString());
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            } catch (Exception e) {
                e.printStackTrace();
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            }
        }
    }

    @Component
    @RabbitListener(queues = "TopicQueue.man")
    public class ConsumerTopicQueueMan {
        @RabbitHandler
        public void process(Map obj, Channel channel, Message message) throws IOException {
            try {
                System.out.println("TopicQueueMan消费者收到消息并ACK返回  : " + obj.toString());
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            } catch (Exception e) {
                e.printStackTrace();
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            }
        }
    }

    @Component
    @RabbitListener(queues = "TopicQueue.woman")
    public class ConsumerTopicQueueWoman {
        @RabbitHandler
        public void process(Map obj, Channel channel, Message message) throws IOException {
            try {
                System.out.println("TopicQueueWoman消费者收到消息并ACK返回  : " + obj.toString());
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            } catch (Exception e) {
                e.printStackTrace();
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            }
        }
    }

}