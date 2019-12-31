十二、整合MybatisPlus
---

### 相关知识

MybatisPlus 是一个 MyBatis 的增强工具，在 MyBatis 的基础上只做增强不做改变，为简化开发、提高效率而生。

Swagger官网：<https://mp.baomidou.com/>

#### 特性： 
 - 无侵入：只做增强不做改变，引入它不会对现有工程产生影响，如丝般顺滑
 - 损耗小：启动即会自动注入基本 CURD，性能基本无损耗，直接面向对象操作
 - 强大的 CRUD 操作：内置通用 Mapper、通用 Service，仅仅通过少量配置即可实现单表大部分 CRUD 操作，更有强大的条件构造器，满足各类使用需求
 - 支持 Lambda 形式调用：通过 Lambda 表达式，方便的编写各类查询条件，无需再担心字段写错
 - 支持主键自动生成：支持多达 4 种主键策略（内含分布式唯一 ID 生成器 - Sequence），可自由配置，完美解决主键问题
 - 支持 ActiveRecord 模式：支持 ActiveRecord 形式调用，实体类只需继承 Model 类即可进行强大的 CRUD 操作
 - 支持自定义全局通用操作：支持全局通用方法注入（ Write once, use anywhere ）
 - 内置代码生成器：采用代码或者 Maven 插件可快速生成 Mapper 、 Model 、 Service 、 Controller 层代码，支持模板引擎，更有超多自定义配置等您来使用
 - 内置分页插件：基于 MyBatis 物理分页，开发者无需关心具体操作，配置好插件之后，写分页等同于普通 List 查询
 - 分页插件支持多种数据库：支持 MySQL、MariaDB、Oracle、DB2、H2、HSQL、SQLite、Postgre、SQLServer 等多种数据库
 - 内置性能分析插件：可输出 Sql 语句以及其执行时间，建议开发测试时启用该功能，能快速揪出慢查询
 - 内置全局拦截插件：提供全表 delete 、 update 操作智能分析阻断，也可自定义拦截规则，预防误操作

### 目标

在 [第二章：SpringBoot集成SpringBootJPA完成CURD](https://gitee.com/gongm_24/spring-boot-tutorial/tree/master/chapter2) 的代码基础上，
替换 spring-data-jpa 为 mybatis-plus，熟悉在 mybatis-plus 下实现单表数据的CURD编码

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

添加 `mybatis-plus` 的依赖
```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.2.0</version>
</dependency>
```

添加后的整体依赖如下，在原来的基础上去除了对 `spring-boot-starter-data-jpa` 的依赖
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

    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-boot-starter</artifactId>
        <version>3.2.0</version>
    </dependency>
</dependencies>
```

#### 配置
配置文件 application.yml 中配置数据源，mybatis-plus 也有很多可配置项，具体参考官方文档
```yaml
spring:
  datasource:
    url: jdbc:mysql://192.168.247.130:3306/test?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&&useSSL=false
    username: app
    password: 123456
```

#### 编码
1. Entity 层代码

 - 类上添加注解 @TableName，如果表名与实体名不一致，可自行设置
 - 主键添加注解 @TableId，可设置主键生成规则，本例使用 `AUTO`，表示使用数据库自增
    - AUTO 数据库自增
    - INPUT 自行输入
    - ID_WORKER 分布式全局唯一ID 长整型类型
    - UUID 32位UUID字符串
    - NONE 无状态(默认值)
    - ID_WORKER_STR 分布式全局唯一ID 字符串类型
 - 日期格式添加注解 @DateTimeFormat(pattern = "yyyy-MM-dd")，用于定义前端入参格式

```java
@Data
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private Integer sex;

    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private LocalDate birthday;

    private Integer level;

}
```

2. Repository 层代码

Mybatis-plus 提供的 BaseMapper 接口已经实现了对单表的增删查改操作以及一些其它常用的方法，可以同使用 JPA 接口一样直接调用，而不用去 mybatis 的配置文件中编写 SQL。

创建接口 UserRepository，继承 BaseMapper 接口，内容如下

 - 接口上添加 @Mapper 注解是为了让 mybatis 自行扫描，也可以通过在启动类上添加 @MapperScan 注解进行代替

```java
@Mapper
public interface UserRepository extends BaseMapper<User> {
}
```

3. Service 层代码

创建接口 UserService，继承 mybatis-plus 提供的 IService 通用接口，IService 中已经实现了基础的 CURD 方法
```java
public interface UserService extends IService<User> {
}
```
创建实现类 UserServiceImpl，继承 mybatis-plus 提供的 ServiceImpl 类，ServiceImpl 类实现了 IService 接口
```java
@Service
public class UserServiceImpl extends ServiceImpl<UserRepository, User> implements UserService {
}
```

4. Controller 层代码

创建 controller 类，实现增删查改交换接口

```java
@RestController
@Slf4j
@AllArgsConstructor
public class UserController {

    private UserService userService;

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        userService.save(user);
        return user;
    }

    @PostMapping("/delete")
    public List<User> delete(Long id) {
        userService.removeById(id);
        return userService.list();
    }

    @PostMapping("/list")
    public List<User> list() {
        return userService.list();
    }

    @PostMapping("/update")
    public List<User> update(User user) {
        userService.updateById(user);
        return userService.list();
    }

}
```

5. 启动类
```java
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
```

### 验证结果

编写测试用例(只写的新增的用例，其它的请自行补充)
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
                .content("{\"mobile\":\"13700000001\",\"upwd\":\"123456\"}")
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

### 结束语

Mybatis-plus 在 mybatis 的基础上实现通用 Mapper 以及通用 Service，极大地简化了开发过程，让开发人员从 SQL 组装中脱离出来，专心处理业务逻辑

### 扩展

#### 实现分页查询

创建自动加载类 MybatisConfiguartion，并注册 PaginationInterceptor 类，即可自动加载分页查询

```java
@Configuration
public class MybatisConfiguartion {

    /**
     * 配置分页插件
     */
    @Bean
    @ConditionalOnClass(BaseMapper.class)
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

}
```

创建 PageBO 用于接收分页入参
```java
@Data
public class PageBO {

    /**
     * 当前页数
     */
    private int pageNum = 1;
    /**
     * 每页显示记录数
     */
    private int pageSize = 20;

}
```

在 Controller 中添加分页查询方法
```java
@PostMapping("/search")
public IPage<User> search(PageBO bo) {
    return userService.page(new Page<>(bo.getPageNum(), bo.getPageSize()));
}
```