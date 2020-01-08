十六、整合Mail发送邮件
---

### 目标

整合 Spring boot 提供的 `spring-boot-starter-mail`，实现本地发送邮件

### 准备工作
#### 获取邮箱的SMTP服务器
以网易邮箱为例，通过以下步骤，可以查看到邮箱的SMTP服务器地址
> 登陆邮箱 -> 设置 -> 选择 POP3/SMTP/IMAP

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

添加 `spring-boot-starter-mail` 的依赖
```xml
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
        <artifactId>spring-boot-starter-mail</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```
#### 配置
 - host：即准备中提到的SMTP服务器，配置到这里
 - username：用于发送邮件的邮箱
 - password：用于发送邮件的邮箱的密码
 - smtp.auth：设置发送邮件需要进行权限校验

```yaml
spring:
  mail:
    host: smtp.126.com
    username: 123456@126.com
    password: xxxx
    properties:
      mail:
        smtp:
          auth: true
```

#### 编码
##### 实体类
```java
@Data
public class Mail {
    // 接收方邮件
    private String email;
    // 主题
    private String subject;
    // 邮件内容
    private String content;
    // 模板
    private String template;
    // 入参
    private Map<String, String> arguments;
}
```

##### Service 层代码

JavaMailSender 是邮件发送封装类，提供了文本类型的 SimpleMailMessage 以及 HTML格式的 MimeMessage

```java
@AllArgsConstructor
@Service
public class MailService {

    private MailProperties properties;
    private JavaMailSender mailSender;

    // 发送普通文本
    public void send(Mail mail) throws Exception {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(properties.getUsername());
        message.setTo(mail.getEmail());
        message.setSubject(mail.getSubject());
        message.setText(mail.getContent());
        mailSender.send(message);
    }

    // 发送HTML格式邮件，可以附加图片及附件
    public void sendHtml(Mail mail) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(properties.getUsername());
        helper.setTo(mail.getEmail());
        helper.setSubject(mail.getSubject());
        helper.setText(
                "<html><body><img src=\"cid:test\" ></body></html>",
                true);
        // 发送图片
        File file = ResourceUtils.getFile("classpath:static/image/test.png");
        helper.addInline("test", file);
        // 发送附件
        file = ResourceUtils.getFile("classpath:static/image/spring.vsdx");
        helper.addAttachment("测试", file);
        mailSender.send(message);
    }

}
```

##### Controller 层代码
```java
@AllArgsConstructor
@RestController
public class MailController {

    private MailService mailService;

    @PostMapping("/sendText")
    public String sendText(@RequestBody Mail mail) {
        try {
            mailService.send(mail);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "OK";
    }

    @PostMapping("/sendHtml")
    public String sendHtml(@RequestBody Mail mail) {
        try {
            mailService.sendHtml(mail);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "OK";
    }

}
```

##### 启动类

```java
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
```

### 验证结果

编写测试用例
```java
@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = Application.class)
public class UserTest {

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testSendText() throws Exception {
        MvcResult mvcResult = mvc.perform(
                MockMvcRequestBuilders
                .post("/sendText")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(
                        "{\"email\":\"123456@qq.com\"," +
                        "\"subject\":\"测试发邮件\"," +
                        "\"content\":\"随便的内容\"}")
        )
        .andDo(MockMvcResultHandlers.print())
        .andReturn();
        Assert.assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void testSendHtml() throws Exception {
        MvcResult mvcResult = mvc.perform(
                MockMvcRequestBuilders
                        .post("/sendHtml")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(
                                "{\"email\":\"123456@qq.com\"," +
                                "\"subject\":\"测试发邮件\"}")
        )
        .andDo(MockMvcResultHandlers.print())
        .andReturn();
        Assert.assertEquals(200, mvcResult.getResponse().getStatus());
    }

}
```

执行后去邮箱查看是否收到发送的测试邮件

### 源码地址

本章源码 : <https://gitee.com/gongm_24/spring-boot-tutorial.git>

### 参考
 - <https://blog.52itstyle.vip/archives/1264/>

### 结束语

发送邮件是生产中必须的功能，此处只是简单地进行邮件发送。
邮件发送因为需要跟邮件服务器进行交互，是一件比较耗时的操作，一般会使用队列进行异步发送，还可以与定时器结合实现定时发送功能。
在扩展中，还将介绍使用模板进行邮件内容的组织。

### 扩展
#### 使用 Freemarker 模板
##### 添加依赖
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-freemarker</artifactId>
</dependency>
```

##### 配置
在 application.yml 中进行配置，配置了 freemarker 模板文件的位置
```yaml
spring
  freemarker:
    template-loader-path: classpath:/templates/
```

##### Service 发送方法代码
```java
public void sendFreemarker(Mail mail) throws Exception {
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true);
    helper.setFrom(properties.getUsername());
    helper.setTo(mail.getEmail());
    helper.setSubject(mail.getSubject());
    Template template = configuration.getTemplate(mail.getTemplate());
    String text = FreeMarkerTemplateUtils.processTemplateIntoString(
            template, mail.getArguments());
    helper.setText(text, true);
    mailSender.send(message);
}
```

##### Controller 方法代码
```java
@PostMapping("/sendFreemarkerTpl")
public String sendFreemarkerTpl(@RequestBody Mail mail) {
    try {
        mailService.sendFreemarker(mail);
    } catch (Exception e) {
        e.printStackTrace();
    }
    return "OK";
}
```
##### 测试用例
```java
@Test
public void testSendFreemarker() throws Exception {
    MvcResult mvcResult = mvc.perform(
            MockMvcRequestBuilders
                    .post("/sendFreemarkerTpl")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content("{\"email\":\"gongm_24@126.com\"," +
                            "\"subject\":\"测试发送Freemarker模块邮件\"," +
                            "\"template\":\"welcome.ftl\"," +
                            "\"arguments\":{\"username\":\"哈哈哈哈\"}}")
    )
    .andDo(MockMvcResultHandlers.print())
    .andReturn();
    Assert.assertEquals(200, mvcResult.getResponse().getStatus());
}
```


#### 使用 thymeleaf 模板
##### 添加依赖
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```
##### 配置
在 application.yml 中进行配置，配置了 thymeleaf 模板文件的位置，注意最后的反斜杠，一定不能少
```yaml
spring
  thymeleaf:
    prefix: classpath:/templates/
```

##### Service 发送方法代码
```java
public void sendThymeleaf(Mail mail) throws Exception {
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true);
    helper.setFrom(properties.getUsername());
    helper.setTo(mail.getEmail());
    helper.setSubject(mail.getSubject());
    Context context = new Context();
    for (Map.Entry<String, String> entry : mail.getArguments().entrySet()) {
        context.setVariable(entry.getKey(), entry.getValue());
    }
    String text = templateEngine.process(mail.getTemplate(), context);
    helper.setText(text, true);
    mailSender.send(message);
}
```

##### Controller 方法代码
```java
@PostMapping("/sendThymeleaf")
public String sendThymeleaf(@RequestBody Mail mail) {
    try {
        mailService.sendThymeleaf(mail);
    } catch (Exception e) {
        e.printStackTrace();
    }
    return "OK";
}
```
##### 测试用例
```java
@Test
public void testSendThymeleaf() throws Exception {
    MvcResult mvcResult = mvc.perform(
            MockMvcRequestBuilders
                    .post("/sendThymeleaf")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content("{\"email\":\"gongm_24@126.com\"," +
                            "\"subject\":\"测试发送Freemarker模块邮件\"," +
                            "\"template\":\"thymeleaf\"," +
                            "\"arguments\":{\"username\":\"哈哈哈哈\"}}")
    )
    .andDo(MockMvcResultHandlers.print())
    .andReturn();
    Assert.assertEquals(200, mvcResult.getResponse().getStatus());
}
```