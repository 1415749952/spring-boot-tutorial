十三、整合fluent-validator优雅业务校验
---

### 相关知识

FluentValidator是一个工具类库，使用流式（Fluent Interface）调用风格让校验跑起来更优雅，代码更简洁，同时验证器（Validator）可以做到开闭原则，实现最大程度的复用。

github地址：<https://github.com/neoremind/fluent-validator>
中文使用手册：<http://neoremind.com/2016/02/java%E7%9A%84%E4%B8%9A%E5%8A%A1%E9%80%BB%E8%BE%91%E9%AA%8C%E8%AF%81%E6%A1%86%E6%9E%B6fluent-validator/>

#### 特性：
 - 验证逻辑与业务逻辑不再耦合，摒弃原来不规范的验证逻辑散落的现象。
 - 校验器各司其职，好维护，可复用，可扩展，一个校验器（Validator）只负责某个属性或者对象的校验，可以做到职责单一，易于维护，并且可复用。
 - 流式风格（Fluent Interface）调用。
 - 使用注解方式验证，可以装饰在属性上，减少硬编码量。
 - 支持JSR 303 – Bean Validation标准，也就是说可以兼容 Hibernate Validator。
 - Spring良好集成
 - 回调给予你充分的自由度，验证过程中发生的错误、异常，验证结果的返回，开发人员都可以定制。

### 目标
替换 hibernate-validator 为 fluent-validator，实现对入参的校验及异常输出

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

添加 `fluent-validator` 的依赖
```xml
<dependency>
	<groupId>com.baidu.unbiz</groupId>
	<artifactId>fluent-validator</artifactId>
	<version>1.0.9</version>
</dependency>
```

因为 spring-boot 默认使用 logback 作为日志输出组件，所以在引入的时候需要去掉 fluent-validate 自身的 slf4j
```xml
<dependency>
    <groupId>com.baidu.unbiz</groupId>
    <artifactId>fluent-validator</artifactId>
    <version>1.0.9</version>
    <exclusions>
        <exclusion>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-log4j12</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```
fluent-validator 集成 hibernate-validator 需要添加依赖
```xml
<dependency>
    <groupId>com.baidu.unbiz</groupId>
    <artifactId>fluent-validator-jsr303</artifactId>
    <version>1.0.9</version>
</dependency>
```
fluent-validator 集成 spring 需要添加依赖
```xml
<dependency>
    <groupId>com.baidu.unbiz</groupId>
    <artifactId>fluent-validator-spring</artifactId>
    <version>1.0.9</version>
</dependency>
```

添加后的整体依赖如下
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <exclusions>
            <exclusion>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-tomcat</artifactId>
            </exclusion>
        </exclusions>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-undertow</artifactId>
    </dependency>

    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <scope>provided</scope>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>

    <dependency>
        <groupId>com.baidu.unbiz</groupId>
        <artifactId>fluent-validator</artifactId>
        <version>1.0.9</version>
        <exclusions>
            <exclusion>
                <groupId>org.slf4j</groupId>
                <artifactId>slf4j-log4j12</artifactId>
            </exclusion>
        </exclusions>
    </dependency>

    <dependency>
        <groupId>com.baidu.unbiz</groupId>
        <artifactId>fluent-validator-jsr303</artifactId>
        <version>1.0.9</version>
    </dependency>

    <dependency>
        <groupId>com.baidu.unbiz</groupId>
        <artifactId>fluent-validator-spring</artifactId>
        <version>1.0.9</version>
    </dependency>
</dependencies>
```

#### 注册 Fluent-validator
fluent-validate 与 spring 结合使用 annotation 方式进行参数校验，需要借助于 spring 的 AOP，fluent-validate 提供了处理类 FluentValidateInterceptor，但是 fluent-validate 提供的默认验证回调类 DefaultValidateCallback 对校验失败的情况并没有处理，所以需要自行实现一个

1. 自定义异常回调类
```java
public static class HussarValidateCallBack extends DefaultValidateCallback implements ValidateCallback {
    @Override
    public void onSuccess(ValidatorElementList validatorElementList) {
        super.onSuccess(validatorElementList);
    }

    @Override
    public void onFail(ValidatorElementList validatorElementList, List<ValidationError> errors) {
        throw new RuntimeException(errors.get(0).getErrorMsg());
    }

    @Override
    public void onUncaughtException(Validator validator, Exception e, Object target) throws Exception {
        throw new RuntimeException(e);
    }
}
```

2. 注册 IOC

注册 ValidateCallback 及 FluentValidateInterceptor，并且配置一个 AOP 规则

```java
@Configuration
public class ValidateConfiguartion {

    @Bean
    public FluentValidateInterceptor fluentValidateInterceptor() {
        FluentValidateInterceptor fluentValidateInterceptor = new FluentValidateInterceptor();
        fluentValidateInterceptor.setCallback(validateCallback());
        return fluentValidateInterceptor;
    }

    @Bean
    public ValidateCallback validateCallback() {
        return new HussarValidateCallBack();
    }

    @Bean
    public BeanNameAutoProxyCreator beanNameAutoProxyCreator(){
        BeanNameAutoProxyCreator proxyCreator = new BeanNameAutoProxyCreator();
        proxyCreator.setBeanNames("*Controller");
        proxyCreator.setInterceptorNames("fluentValidateInterceptor");
        return proxyCreator;
    }
}
```

3. 全局异常处理
```java
@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

    /**
     * 校验错误拦截处理
     * 使用 @RequestBody 接收入参时，校验失败抛 MethodArgumentNotValidException 异常
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public RestData handler(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException handler", e);
        BindingResult bindingResult = e.getBindingResult();
        if (bindingResult.hasFieldErrors()) {
            return RestData.build().error(bindingResult.getFieldError().getDefaultMessage());
        }
        return RestData.build().error("parameter is not valid");
    }

    /**
     * 校验错误拦截处理
     * 使用 @RequestBody 接收入参时，数据类型转换失败抛 HttpMessageConversionException 异常
     */
    @ExceptionHandler(value = HttpMessageConversionException.class)
    public RestData handler(HttpMessageConversionException e) {
        log.error("HttpMessageConversionException handler", e);
        return RestData.build().error(e.getMessage());
    }

    /**
     * 全局异常处理
     */
    @ExceptionHandler(value = Exception.class)
    public RestData handler(Exception e) {
        log.error("exception handler", e);
        return RestData.build().error(e.getMessage());
    }

}
```

#### 编码
1. 添加校验规则

为业务类添加校验规则，此处，并没有添加 fluent-validate 注解，而是保持了原来的 hibernate-validate
```java
@Getter
@Setter
public class UserBO {

    @NotBlank(message = "手机号不能为空")
    private String mobile;

    @NotBlank(message = "密码不能为空")
    @Length(min = 6, max = 16, message = "密码长度必须在6-16位之间")
    private String upwd;

}
```

2. 使用校验

在 Controller 接口的参数前面添加 @FluentValid 注解，替换掉 hibernate-validate 的 @Valid 注解

```java
@RestController
@Slf4j
public class UserController {

    @PostMapping("/register")
    public RestData<UserBO> register(@FluentValid @RequestBody UserBO userBo) {
        return new RestData<UserBO>().success("", userBo);
    }

}
```

3. 启动类
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
    public void test1() throws Exception {
        MvcResult mvcResult = mvc.perform(
                MockMvcRequestBuilders
                .post("/register")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content("{\"upwd\":\"123456\"}")
        )
//        .andExpect(status().isOk());
//        .andExpect(content().string("hello"))
        .andDo(MockMvcResultHandlers.print())
        .andReturn();

        Assert.assertEquals(200, mvcResult.getResponse().getStatus());
    }

}
```

返回结果为
```
MockHttpServletResponse:
           Status = 200
    Error message = null
          Headers = {Content-Type=[application/json;charset=UTF-8]}
     Content type = application/json;charset=UTF-8
             Body = {"status":false,"info":"手机号不能为空","data":null,"success":false}
```

### 源码地址

本章源码 : <https://gitee.com/gongm_24/spring-boot-tutorial.git>

### 结束语

fluent-validate 可以全方位兼容 hibernate-validate，基于 spring 的 AOP 可以提供基于注解的方法入参校验，同时也可以提供流式编程的工具类业务校验，替代 hibernate-validate 的同时提供了更多扩展性。

### 扩展

#### 实现自定义校验

1. 实现自定义校验类

```java
public class SexValidator extends ValidatorHandler<Integer> implements Validator<Integer> {

    @Override
    public boolean validate(ValidatorContext context, Integer t) {
        if (t < 0 || t > 2) {
            // 构建详细错误信息
            context.addError(ValidationError.create("sex值不正确").setErrorCode(100).setField("sex").setInvalidValue(t));
            // 简单处理，直接放入错误消息
            // context.addErrorMsg("sex值不正确");
            return false;
        }
        return true;
    }

}
```

Validator 接口定义了三个方法，解释如下
```java
public interface Validator<T> {
 
 /**
 * 判断在该对象上是否接受或者需要验证
 * <p/>
 * 如果返回true，那么则调用{@link #validate(ValidatorContext, Object)}，否则跳过该验证器
 *
 * @param context 验证上下文
 * @param t 待验证对象
 *
 * @return 是否接受验证
 */
 boolean accept(ValidatorContext context, T t);
 
 /**
 * 执行验证
 * <p/>
 * 如果发生错误内部需要调用{@link ValidatorContext#addErrorMsg(String)}方法，也即<code>context.addErrorMsg(String)
 * </code>来添加错误，该错误会被添加到结果存根{@link Result}的错误消息列表中。
 *
 * @param context 验证上下文
 * @param t 待验证对象
 *
 * @return 是否验证通过
 */
 boolean validate(ValidatorContext context, T t);
 
 /**
 * 异常回调
 * <p/>
 * 当执行{@link #accept(ValidatorContext, Object)}或者{@link #validate(ValidatorContext, Object)}发生异常时的如何处理
 *
 * @param e 异常
 * @param context 验证上下文
 * @param t 待验证对象
 */
 void onException(Exception e, ValidatorContext context, T t);
 
}
```

2. 使用自定义校验

添加 sex 属性，使用 @FluentValidate 注解标记使用自定义校验

```java
@Getter
@Setter
public class UserBO {

    @NotBlank(message = "手机号不能为空")
    private String mobile;

    @NotBlank(message = "密码不能为空")
    @Length(min = 6, max = 16, message = "密码长度必须在6-16位之间")
    private String upwd;

    @FluentValidate({SexValidator.class})
    private Integer sex;

}
```

#### 级联验证

在对象属性上添加 @FluentValid 注解，代替 hibernate-validate 的 @Valid。
```java
public class Garage {
 
    @FluentValidate({CarNotExceedLimitValidator.class})
    @FluentValid
    private List<Car> carList;
    
}
```