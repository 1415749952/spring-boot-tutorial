十九、整合RabbitMQ之延迟队列
---

### 相关知识
#### 什么是延迟队列
队列中的消息在等待指定时间后，消费者才能够进行消费。

#### 应用场景
 - 商城系统，下单后半个小时未付款，自动取消订单
 
#### 实现方式
RabbitMQ 本身没有直接支持延迟队列功能，但是通过控制消息的生存时间及死信队列，可以模拟出延迟队列的效果。

RabbitMQ 控制消息的生存时间有两种方法：
 - 设置队列属性（x-message-ttl），队列中所有消息都有相同的过期时间
 - 设置消息属性（expiration），拥有单独的过期时间
 
关于死信队列可以参考 <https://gitee.com/gongm_24/spring-boot-tutorial/tree/master/chapter18>

### 目标
整合 Spring boot 提供的 `spring-boot-starter-amqp`，实现延迟队列

### 操作步骤
#### 添加依赖
引入 Spring Boot Starter 父工程
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.0.5.RELEASE</version>
</parent>
```

添加 `spring-boot-starter-amqp` 的依赖
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

添加后的整体依赖如下
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <scope>provided</scope>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-amqp</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```
#### 编码(发送方)
#### 配置
```yaml
spring:
  rabbitmq:
    host: 127.0.0.1
    port: 5672
    username: admin
    password: admin
```
##### 定义队列
定义一个测试队列 TestDeadQueue，并为该队列配置死信队列，
配置的方法就是在声明队列的时候，添加参数 `x-dead-letter-exchange` 及 `x-dead-letter-routing-key`，
其实就是在消费失败时，将消息使用该 exchange 及 routing 发送至指定队列
```java
@Configuration
public class DeadConfig {

    @Bean
    public Queue testDelayQueue() {
        Map<String, Object> params = new HashMap<>();
        params.put("x-dead-letter-exchange", "DeadExchange");
        params.put("x-dead-letter-routing-key", "DeadRouting");
        // 如果设置，则队列中所有消息的过期时间都是 5 秒，如果希望在每个消息中进行单独设置，则不能设置
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
```

##### 测试发送
```java
@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = SenderApplication.class)
public class MqTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    // 队列设置了过期时间
    @Test
    public void testDelayQueue() throws Exception {
        String msgId = String.valueOf(UUID.randomUUID());
        String sendTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String,Object> map = new HashMap<>(4);
        map.put("msgId", msgId);
        map.put("msgData", "test delay queue");
        map.put("sendTime", sendTime);
        rabbitTemplate.convertAndSend("TestDelayExchange", "TestDelayRouting", map);
    }

    // 对消息设置过期时间，队列不用设置过期时间
    @Test
    public void testDelayQueueWithMessage() throws Exception {
        String msgId = String.valueOf(UUID.randomUUID());
        String sendTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String,Object> map = new HashMap<>(4);
        map.put("msgId", msgId);
        map.put("msgData", "test dead letter queue");
        map.put("sendTime", sendTime);
        rabbitTemplate.convertAndSend("TestDelayExchange", "TestDelayRouting", map, msg -> {
            msg.getMessageProperties().setExpiration("5000");
            return msg;
        });
    }

}
```

### 源码地址
本章源码 : <https://gitee.com/gongm_24/spring-boot-tutorial.git>

### 结束语
总结一下延迟消息的生命周期：
```mermaid 
flowchat
st=>start: 客户端发送消息
e=>end: 结束
op1=>operation: 业务队列
op2=>operation: 延迟队列
op3=>operation: 等待消息过期
op4=>operation: 死信队列
op5=>operation: 消费消息

st->op1->op2->->op3->op4->op5
```
