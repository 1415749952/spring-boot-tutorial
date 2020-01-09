二十、整合RabbitMQ实现异步发送邮件
---

### 相关知识
#### 什么是死信队列
“死信”是RabbitMQ中的一种消息机制，当你在消费消息时，如果队列里的消息出现以下情况：
 - 消息被否定确认，使用 channel.basicNack 或 channel.basicReject ，并且此时requeue 属性被设置为false。
 - 消息在队列的存活时间超过设置的TTL时间。
 - 消息队列的消息数量已经超过最大队列长度。

那么该消息将成为“死信”，“死信”消息会被RabbitMQ进行特殊处理。
如果配置了死信队列信息，那么该消息将会被丢进死信队列中，如果没有配置，则该消息将会被丢弃。

#### 应用场景
当一个队列中的消息必须要被正确消费，但是却无法被正确消费，有很多情况可能导致出现这个问题，
可能是消息本身的数据导致业务无法通过校验，也可能是相关服务宕机导致，甚至可能只是网络波动导致。
此时该消息不能确认，也不能丢弃，更不能一直 requeue，那将变成一个死循环，
这种消息不能一直呆在当前业务队列阻塞其它消息，但又需要处理，把它们导航至死信队列就是一种解决办法，
通过订阅死信队列可以对此类消息进行例外处理，例如通知相关人员检查数据进行人工处理。

### 目标
整合 Spring boot 提供的 `spring-boot-starter-amqp`，实现消息消费失败转死信队列

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
```

##### 测试发送
```java
@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = SenderApplication.class)
public class MqTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testDeadQueue() throws Exception {
        String msgId = String.valueOf(UUID.randomUUID());
        String sendTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String,Object> map = new HashMap<>(4);
        map.put("msgId", msgId);
        map.put("msgData", "test dead letter queue");
        map.put("sendTime", sendTime);
        rabbitTemplate.convertAndSend("TestDeadExchange", "TestDeadRouting", map);
    }

}
```

#### 编码(消费方)
##### 配置
配置消费消息使用手动确认模式
```yaml
spring:
  rabbitmq:
    host: 127.0.0.1
    port: 5672
    username: admin
    password: admin
    listener:
      type: simple
      simple:
        acknowledge-mode: manual
```
##### 定义队列
```java
@Configuration
public class Config {

    @Bean
    public Queue testDirectQueue() {
        Map<String, Object> params = new HashMap<>();
        params.put("x-dead-letter-exchange", "TestDeadExchange");
        params.put("x-dead-letter-routing-key", "TestDeadRouting");
        return new Queue("TestDeadQueue",true, false, false, params);
    }

    @Bean
    public Queue TestDeadQueue() {
        return new Queue("DeadQueue",true);
    }

}
```

##### 消费
定义队列 TestDirectQueue 的消费方法，在方法中调用了 basicReject 方法，用于告诉 RabbitMQ 消费失败，
调用 basicReject 方法时 requeue 参数必须为 false，不然就会把消息重新加入当前队列，
由于队列 TestDirectQueue 配置了死信队列，于是 RabbitMQ 在接收到消费失败的 ACK 后，
将当前消息根据配置的 exchange 及 routing 进行再次发送，并从当前队列中删除，
消息最终被发送至队列 DeadQueue。

```java
@Component
public class ConsumerWithAck {

    @RabbitListener(queues = "TestDeadQueue")
    @RabbitHandler
    public void process(Map obj, Channel channel, Message message) throws IOException {
        try {
            System.out.println("DirectQueue消费者收到消息并NACK返回  : " + obj.toString());
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            e.printStackTrace();
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
        }
    }

    @RabbitListener(queues = "DeadQueue")
    @RabbitHandler
    public void processDead(Map obj, Channel channel, Message message) throws IOException {
        try {
            System.out.println("DeadQueue消费者收到消息并ACK返回  : " + obj.toString());
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            e.printStackTrace();
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
        }
    }

}
```

### 源码地址
本章源码 : <https://gitee.com/gongm_24/spring-boot-tutorial.git>

### 结束语
死信队列其实并没有什么神秘的地方，不过是绑定在死信交换机上的普通队列，而死信交换机也只是一个普通的交换机，不过是用来专门处理死信的交换机。

总结一下死信消息的生命周期：
```mermaid 
flowchat
st=>start: 客户端发送消息
e=>end: 结束
op1=>operation: 业务队列
op2=>operation: 消费者消费
op3=>operation: 死信队列
cond1=>condition: 是否消费成功
cond2=>condition: 队列是否配置死信队列

st->op1->op2->cond1
cond1(yes)->e
cond1(no)->cond2
cond2(yes)->op3
cond2(no, bottom)->e
```
