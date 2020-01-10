二十、整合RabbitMQ实现定时发送邮件
---

### 目标
整合 RabbitMQ 及 Mail 实现异步发送邮件及定时发送邮件。

流程如下：
```mermaid 
flowchat
st=>start: 客户端发送邮件
e=>end: 消费消息
cond1=>condition: 是否定时
op1=>operation: 发送即时邮件
op2=>operation: 发送定时邮件
op3=>operation: 邮件队列(MailQueue)
op4=>operation: 延迟邮件队列(EmailDelayQueue)
cond2=>condition: 是否到期

st->cond1
cond1(no)->op1->op3->e
cond1(yes, bottom)->op2->op4->cond2
cond2(yes)->op3->e
```

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

添加 RabbitMQ 及 Mail 的依赖
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
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
        <artifactId>spring-boot-starter-mail</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

#### 编码(消费方)
消费方监听队列 `EmailQueue`，实现发送邮件

##### 配置
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
  mail:
    host: smtp.126.com
    username: gongm_24@126.com
    password: hmily52mg@2014
    properties:
      mail:
        smtp:
          auth: true
```

##### 定义队列
```java
@Configuration
public class MqConfig {

    @Bean
    public Queue emailQueue() {
        return new Queue("EmailQueue",true);
    }
}
```

##### 监听队列实现邮件发送
```java
@AllArgsConstructor
@Component
public class EmailConsumer {

    private MailProperties properties;
    private JavaMailSender mailSender;

    @RabbitListener(queues = "EmailQueue")
    @RabbitHandler
    public void process(Channel channel, Message message) throws IOException {
        try {
            Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
            Email email = (Email) converter.fromMessage(message);
            doSend(email);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            e.printStackTrace();
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
        }
    }

    public void doSend(Email mail) throws Exception {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(properties.getUsername());
        message.setTo(mail.getTo());
        message.setSubject(mail.getSubject());
        message.setText(mail.getContent());
        mailSender.send(message);
    }

}
```

#### 编码(发送方)
##### 配置
```yaml
spring:
  rabbitmq:
    host: 127.0.0.1
    port: 5672
    username: admin
    password: admin
```
##### 定义队列
定义一个邮件队列，消费端监听该队列，实现邮件异步发送
定义一个延迟邮件队列，该队列中的消息到期则自动转入邮件队列。
```java
@Configuration
public class MqConfig {

    @Bean
    public Queue emailQueue() {
        return new Queue("EmailQueue",true);
    }

    @Bean
    DirectExchange emailExchange() {
        return new DirectExchange("EmailExchange");
    }

    @Bean
    Binding bindingEmailQueue() {
        return BindingBuilder.bind(emailQueue()).to(emailExchange()).with("EmailRouting");
    }

    @Bean
    public Queue emailDelayQueue() {
        Map<String, Object> params = new HashMap<>(2);
        params.put("x-dead-letter-exchange", "EmailExchange");
        params.put("x-dead-letter-routing-key", "EmailRouting");
        return new Queue("EmailDelayQueue",true, false, false, params);
    }

    @Bean
    DirectExchange emailDelayExchange() {
        return new DirectExchange("EmailDelayExchange");
    }

    @Bean
    Binding bindingEmailDelayQueue() {
        return BindingBuilder.bind(emailDelayQueue()).to(emailDelayExchange()).with("EmailDelayRouting");
    }

}
```

##### Service 层代码
```java
@AllArgsConstructor
@Service
public class MailService {

    private RabbitTemplate rabbitTemplate;
    // 即时发送
    public void send(String to, String subject, String content) {
        Email mail = new Email();
        mail.setTo(to);
        mail.setSubject(subject);
        mail.setContent(content);
        rabbitTemplate.convertAndSend("EmailExchange", "EmailRouting", mail);
    }
    // 定时发送
    public void send(String to, String subject, String content, LocalDateTime time) {
        Email mail = new Email();
        mail.setTo(to);
        mail.setSubject(subject);
        mail.setContent(content);
        rabbitTemplate.convertAndSend("EmailDelayExchange", "EmailDelayRouting", mail, msg -> {
            Duration duration = Duration.between(LocalDateTime.now(), time);
            msg.getMessageProperties().setExpiration(String.valueOf(duration.toMillis()));
            return msg;
        });
    }

}
```

### 源码地址
本章源码 : <https://gitee.com/gongm_24/spring-boot-tutorial.git>

