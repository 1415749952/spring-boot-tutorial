十七、整合RabbitMQ消息队列与ACK消息确认
---

### 相关知识

#### 元素
 - 交换器
 - 队列
 - 绑定
 - 虚拟主机

#### 几个重要的交换器
 - direct：一对一
 - fanout：一对多
 - topic：一对多匹配

### 目标
整合 Spring boot 提供的 `spring-boot-starter-amqp`，实现消息发送、消息消费、确认

### 准备工作
#### 安装RabbitMQ
介绍使用 Docker 方式安装，Docker 安装可以参考 <https://blog.csdn.net/gongm24/article/details/86357866>
##### 下载镜像
```
docker pull rabbitmq:management
```
##### 运行镜像
设置默认用户名及密码
```
docker run --name rabbitmq \
    -p 15672:15672 \
    -p 5672:5672 \
    -e RABBITMQ_DEFAULT_USER=admin \
    -e RABBITMQ_DEFAULT_PASS=admin \
    -d rabbitmq:management
```
##### 登录管理界面
访问地址: `http://[宿主机IP]:15672`，使用用户名密码 `admin/admin` 进行登录

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
#### 配置

```yaml
spring:
  rabbitmq:
    host: 127.0.0.1
    port: 5672
    username: admin
    password: admin
#虚拟host 可以不设置,使用server默认host
#    virtual-host: JCcccHost
```

#### 编码(消息发送方)
##### 定义 Exchange、Queue并将两者进行关联
```java
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
```

##### Controller 层代码

引入 `spring-boot-starter-amqp` 时，会自动注册 RabbitTemplate 到 Spring 容器，
消息发送可以借助其提供的 `convertAndSend` 方法

```java
@AllArgsConstructor
@RestController
public class MqController {

    private RabbitTemplate rabbitTemplate;

    @PostMapping("/sendDirectMessage")
    public String sendDirectMessage(@RequestBody String msgData) {
        String msgId = String.valueOf(UUID.randomUUID());
        String sendTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String,Object> map = new HashMap<>(4);
        map.put("msgId", msgId);
        map.put("msgData", msgData);
        map.put("sendTime", sendTime);
        rabbitTemplate.convertAndSend("TestDirectExchange", "TestDirectRouting", map);
        return "ok";
    }

}
```

##### 启动类
```java
@SpringBootApplication
public class SenderApplication {

    public static void main(String[] args) {
        SpringApplication.run(SenderApplication.class, args);
    }

}
```

##### 执行消费发送
执行测试用例，执行完成后，去 RabbitMQ 管理后台查看，
在 Exchanges 标签页中，可以看到使用的交换器 `TestDirectExchange`，
在 Queues 标签页中，可以看到队列 `TestDirectQueue`，并且 Ready 值为 1，表示有一条数据待处理
```yaml
@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = SenderApplication.class)
public class MqTest {

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testSendDirectMessage() throws Exception {
        MvcResult mvcResult = mvc.perform(
                MockMvcRequestBuilders
                .post("/sendDirectMessage")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content("test send direct message")
        )
        .andDo(MockMvcResultHandlers.print())
        .andReturn();
        Assert.assertEquals(200, mvcResult.getResponse().getStatus());
    }

}
```


#### 编码(消息消费方)
##### 定义 Queue
```java
@Configuration
public class Config {

    @Bean
    public Queue TestDirectQueue() {
        return new Queue("TestDirectQueue",true);
    }

}
```

##### 定义消费类
 - 在类上使用注解 @RabbitListener，声明监听的队列
 - 在处理方法上使用注解 @RabbitHandler，标记该方法为回调处理方法
```java
@Component
@RabbitListener(queues = "TestDirectQueue")
public class MqConsumer {

    @RabbitHandler
    public void process(Map testMessage) {
        System.out.println("DirectReceiver消费者收到消息  : " + testMessage.toString());
    }

}
```

##### 启动类
```java
@SpringBootApplication
public class ConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }

}
```

##### 测试
使用 ConsumerApplication 类启动项目，在日志中可以看到消费消息时产生的日志
```
DirectReceiver消费者收到消息  : {msgId=ccf1f1c0-f8c5-483a-933e-ed3d77d59333, msgData=null, sendTime=2020-01-07 19:50:23}
```

去 RabbitMQ 管理后台，查看 Queues 标签页，可以看到队列 `TestDirectQueue` 的 Ready 值变成了 0，表示消息已经被消费。

#### 编码(消息消费方实现消息确认)
消息接收的确认机制主要存在三种模式：
 - 自动确认，默认值，RabbitMQ 将消息发送给应用程序，即认为消费成功，如果应用程序在消费消息的过程中，发生异常，RabbitMQ 是无法感知的，依然会将该消息从队列中删除，但实际上应用程序又没有消费成功，相当于丢失了消息。
 - 不确认，忽略
 - 手动确认，实际生产多数选择的模式，应用程序接收到消息并进行处理后，返回一个响应（ACK），RabbitMQ 接收到这个响应后，判断是消费成功，还是失败，并调用相应的回调方法进行处理。
    - basic.ack 用于肯定确认 
    - basic.nack 用于否定确认 
    - basic.reject 用于否定确认，但与 basic.nack 相比有一个限制，一次只能拒绝单条消息 

##### 配置
```yaml
spring:
  rabbitmq:
    host: 127.0.0.1
    port: 5672
    username: admin
    password: admin
    # 虚拟host 可以不设置,使用server默认host
    #virtual-host: JCcccHost
    # 发送方消息确认：已发送到交换机(Exchange)
    publisher-confirms: true
    # 发送方消息确认：已发送到队列(Queue)
    publisher-returns: true
    # 消费方消息确认：手动确认
    listener:
      type: simple
      simple:
        acknowledge-mode: manual
```

##### 定义消费类
basicReject 的第二个参数是 requeue，意思是是否重新加入队列，
如果为 true，则表示本次消费不成功，并将当前消息重新加入至当前队列，
如果为 false，则表示本次消费不成功，并将当前消息丢弃，如果有设置死信队列，则会进入死信队列（关于死信队列，在下一章会讲）
```java
@Component
@RabbitListener(queues = "TestDirectQueue")
public class ConsumerDirectQueue {
    @RabbitHandler
    public void process(Map obj, Channel channel, Message message) throws IOException {
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            System.out.println("DirectQueue消费者收到消息并ACK返回  : " + obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }
}
```

### 源码地址
本章源码 : <https://gitee.com/gongm_24/spring-boot-tutorial.git>

### 结束语
消息队列是实际生产中是必备组件，用于保证系统高可用、高性能、可扩展
 - 解耦：类比观察者模式，比如用户注册成功后，需要记录日志、发送短信，
原始操作就是在用户注册成功后，分别调用记录日志及发送短信的方法，
使用消息队列后，则可以在用户注册成功后，发送一个注册成功的消息，而注册成功的后续操作则可以订阅该消息队列，分别实现各自的处理逻辑。
如果业务变更，增加了新业务，则不用修改原来的代码，而只需要增加一个订阅即可。
 - 异步：用户注册成功后需要发送短信，而发送短信是一个非常耗时的操作，
这不仅会影响到用户体验，因为短信发送过程中，程序长时间阻塞，还可能造成后台资源紧张。
使用消息队列，异步处理短信发送，则可以完美解决这个问题。这个保证系统高性能的一种常用手段。
 - 削峰/限流：用户注册成功后需要发送短信，而发送短信是一个非常耗时的操作，如果此时有大量请求请求该接口，可能会导致系统挂掉。
使用消息队列，异步进行处理，则可以避免这个问题，如果流量特别大，异步一个一个处理都会导致系统宕机，还可以限流，
在发送消息时先检测队列是否已满，如果已满，则直接给前端返回错误。

### 扩展
#### 发送 Fanout 消息
##### 定义队列
Fanout 交换器没有 Routing，直接将队列与交换器进行关联即可
```java
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
```

##### 发送方法
也是调用 convertAndSend 方法，只是 routing 参数传值 null
```java
@PostMapping("/sendFanoutMessage")
public String sendFanoutMessage(@RequestBody String msgData) {
    String msgId = String.valueOf(UUID.randomUUID());
    String sendTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    Map<String,Object> map = new HashMap<>(4);
    map.put("msgId", msgId);
    map.put("msgData", msgData);
    map.put("sendTime", sendTime);
    rabbitTemplate.convertAndSend("TestFanoutExchange", null, map);
    return "ok";
}
```

#### 发送 Topic 消息
##### 定义队列
```java
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
```

##### 发送方法
跟使用 direct 交换器一样，只是 topic 交互器会根据 routing 进行匹配，然后决定将消息发送至哪些队列
```java
@PostMapping("/sendTopicMessage")
public String sendTopicMessage(@RequestBody String msgData) {
    String msgId = String.valueOf(UUID.randomUUID());
    String sendTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    Map<String,Object> map = new HashMap<>(4);
    map.put("msgId", msgId);
    map.put("msgData", msgData);
    map.put("sendTime", sendTime);
    rabbitTemplate.convertAndSend("TestFanoutExchange", "TopicQueue.woman", map);
    return "ok";
}
```