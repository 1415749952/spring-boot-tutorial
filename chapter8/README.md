第八章：SpringBoot整合hibernate-validator进行参数校验
---

`spring-boot-starter-web` 项目中默认已经集成了 `hibernate-validator`

### 相关知识

#### 常用注解

> JSR 303 Bean Validation

注解|说明|数据类型
---|---|---
AssertTrue|标注元素必须为true|Boolean
AssertFalse|标注元素必须为false|Boolean
DecimalMax(value,isclusive)|标注元素必须小于等于指定值|BigDecimal,BigInteger, CharSequence,byte,short, int, long,Byte,Short, Integer,Long
DecimalMin(value,isclusive)|标注元素必须大于等于指定值|BigDecimal,BigInteger, CharSequence,byte,short, int, long,Byte,Short, Integer,Long
Digits(integer,fraction)|标注元素必须位于指定位数之内|BigDecimal,BigInteger, CharSequence,byte,short, int, long,Byte,Short, Integer,Long
Email(regexp,flags)|标注元素必须为格式正确的邮件地址|CharSequence
Future|标注元素必须为将来的日期|Date,Calendar,Instant, LocalDate,LocalDateTime, LocalTime,MonthDay, OffsetDateTime,OffsetTime, Year,YearMonth, ZonedDateTime,HijrahDate, JapaneseDate,MinguoDate, ThaiBuddhistDate
FutureOrPresent|标注元素必须为现在或将来的日期|同Future
Max(value)|标注元素必须小于等于指定值|BigDecimal,BigInteger, CharSequence,byte,short, int, long,Byte,Short, Integer,Long
Min(value)|标注元素必须大于等于指定值|BigDecimal,BigInteger, CharSequence,byte,short, int, long,Byte,Short, Integer,Long
Negative|标注元素必须为严格负值|BigDecimal,BigInteger, CharSequence,byte,short, int, long,Byte,Short, Integer,Long
NegativeOrZero|标注元素必须为严格的负值或者0值|BigDecimal,BigInteger, CharSequence,byte,short, int, long,Byte,Short, Integer,Long
NotBlank|标注元素必须不为null，且必须包含至少一个非空字符|CharSequence
NotEmpty|标注元素必须不为null，且必须包含至少一个子元素|CharSequence,Collection,Map,Array
NotNull|标注元素必须不为null|all
Null|标注元素必须为null|all
Past|标注元素必须为过去的日期|同Future
PastOrPresent|标注元素必须为过去的或者现在的日期|同Future
Pattern(regexp,flags)|标注元素必须匹配给定的正则表达式|CharSequence
Positive|标注元素必须为严格的正值|BigDecimal,BigInteger, CharSequence,byte,short, int, long,Byte,Short, Integer,Long
PositiveOrZero|标注元素必须为严格的正值或者0值|BigDecimal,BigInteger, CharSequence,byte,short, int, long,Byte,Short, Integer,Long
Size(min,max)|标注元素必须在指定范围之内|CharSequence,Collection,Map,Array

> Hibernate Validation

注解|说明|备注
---|---|---
Length(min,max)|标注元素的长度必须在指定范围之内，包含最大值|字符串
Range(min,max)|标注元素值必须在指定范围之内|数字值，或者其字符串形式
URL(regexp,flags)|标注元素必须为格式正确的URL|字符串
URL(protocol,host,port)|标注元素必须满足给定的协议主机和端口号|字符串

### 课程目标

本章将在[SpringBoot集成SpringBootJPA完成CURD]()的基础上，整合 hibernate-validator，对入参进行校验。

### 操作步骤

#### 添加依赖

`spring-boot-starter-web` 已经默认添加对 `hibernate-validator` 的依赖

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
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
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
</dependencies>
```

#### 编码

1. 为实体类添加校验规则

```java
@Getter
@Setter
public class UserBO {

    /**
     * 用户名，长度在6-16个字符之间，必须参数
     */
    @NotBlank(message = "用户名不能为空")
    @Length(min = 6, max = 16, message = "用户名长度必须在6-16个字符之间")
    private String username;

    /**
     * 出生日期，格式为 yyyy-MM-dd，必须为过去的日期，不必须参数
     */
    @Past(message = "出生日期必须早于当前日期")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private LocalDate birthday;

    /**
     * 等级，整数，0-5之间，必须参数
     */
    @NotNull(message = "用户等级不能为空")
    @Min(value = 0, message = "用户等级最小为0")
    @Max(value = 5, message = "用户等级最大为5")
    @Digits(integer = 1, fraction = 0, message = "用户等级必须为整数")
    private Integer level;

}
```

2. 使用校验

在 Controller 接口的参数前面添加 @Valid 注解，入参注入时便会自动进行规则校验，如果校验成功，则执行方法体，如果校验失败，有两种处理方法。

 - 在参数列表的最后面添加一个 BindingResult 对象获取校验结果，自行组织输出内容。
 - 不使用 BindingResult 对象，则框架抛出异常，通过异常处理机制可以进行捕获，组织输出内容。

```java
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserRepository userRepository;

    /**
     * 不使用 BindingResult 接收校验结果，Spring 将抛出异常
     */
    @PostMapping("/add1")
    public List<User> add1(@Valid @RequestBody User user) {
        userRepository.save(user);
        return userRepository.findAll();
    }

    /**
     * 使用 BindingResult 接收校验结果，自行组织输出内容
     */
    @PostMapping("/add2")
    public List<User> add2(@Valid @RequestBody User user, BindingResult result) {
        if (result.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = result.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
                sb.append(",");
            }
            log.debug(sb.toString());
            return null;
        }
        userRepository.save(user);
        return userRepository.findAll();
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
                .post("/user/add1")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content("{\"name\":\"user1\",\"sex\":1,\"birthday\":\"2030-05-21\"}")
        )
//        .andExpect(status().isOk());
//        .andExpect(content().string("hello"))
        .andDo(MockMvcResultHandlers.print())
        .andReturn();

        Assert.assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void test2() throws Exception {
        MvcResult mvcResult = mvc.perform(
                MockMvcRequestBuilders
                        .post("/user/add2")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content("{\"name\":\"user1\",\"sex\":1,\"birthday\":\"2030-05-21\"}")
        )
//        .andExpect(status().isOk());
//        .andExpect(content().string("hello"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        Assert.assertEquals(200, mvcResult.getResponse().getStatus());
    }

}
```

### 源码地址

本章源码 : <https://gitee.com/gongm_24/spring-boot-tutorial.git>

### 总结

一个安全的接口需要对每一个入参进行校验，以保证参数合法性。

### 参考资料

 - <http://www.cnblogs.com/mr-yang-localhost/p/7812038.html>
 - <https://blog.csdn.net/qq_22845447/article/details/84034289>
 - <https://cloud.tencent.com/developer/article/1054194>
 - <https://www.cnblogs.com/V1haoge/p/9953744.html>
 
### 扩展

#### 自定义校验

1. 创建一个注解，其中 @Constraint 用于指向该注册将使用的自定义校验类

```java
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = ValueRangeValidator.class)
public @interface ValueRange {

    String[] values();

    String message() default "值不正确";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}
```

2. 创建自定义校验类

```java
public class ValueRangeValidator implements ConstraintValidator<ValueRange, Object> {

    private String[] values;

    @Override
    public void initialize(ValueRange constraintAnnotation) {
        values = constraintAnnotation.values();
    }

    /**
     * 校验函数
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        for (String s : values) {
            if (Objects.equals(s, value)) {
                return true;
            }
        }
        return false;
    }

}
```

3. 使用自定义校验

```java
@Getter
@Setter
public class UserBO {

    /**
     * 用户名，长度在6-16个字符之间，必须参数
     */
    @NotBlank(message = "用户名不能为空")
    @Length(min = 6, max = 16, message = "用户名长度必须在6-16个字符之间")
    private String username;

    /**
     * 出生日期，格式为 yyyy-MM-dd，必须为过去的日期，不必须参数
     */
    @Past(message = "出生日期必须早于当前日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    /**
     * 等级，整数，0-5之间，必须参数
     */
    @NotNull(message = "用户等级不能为空")
    @Min(value = 0, message = "用户等级最小为0")
    @Max(value = 5, message = "用户等级最大为5")
    @Digits(integer = 1, fraction = 0, message = "用户等级必须为整数")
    private Integer level;

    /**
     * 限定性别的值只能是 0、1、2
     */
    @ValueRange(values = {"0", "1", "2"})
    private Integer sex;

}
```
