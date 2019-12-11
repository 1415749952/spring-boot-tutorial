第十章：SpringBoot整合MapStruct简化属性复制
---

在之前章节的例子中，我们在接收参数的时候都是使用的 User 类对象，这是一个 DO 对象，
但是这个对象与前端业务通常不能对等，所以更多时候，会创建一个 BO 类用来接收入参。
那么这时就会涉及到 BO 属性值复制至 DO，然后代码里就会出现大量的 `do.set(bo.get())`，
这时，对反射比较了解的同学应该就会尝试使用反射来解决这个问题，虽然可以解决问题，但是反射会降低程序性能，
而 MapStruct 则是另外一种解决方案。

### 相关知识

MapStruct官网：<http://mapstruct.org>

### 课程目标

SpringBoot 整合 MapStruce 以及 Lombok

### 操作步骤

本文使用 Idea 集成开发环境

#### 环境准备

1. 安装 MapStruce 插件

File -> settings，打开 Idea 的设置界面，从左侧栏选择 Plugins 选项，再在右侧查询 MapStruce，点击安装。

2. 设置IDE

File -> Settings 打开设置界面，
选择 Build,Execution,Deployment -> Compiler -> Annotation Processors 进入设置界面，
勾选 enable annotation processing

#### 添加依赖

引入 Spring Boot Starter 父工程

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.0.5.RELEASE</version>
</parent>
```

添加 `mapstruct` 的依赖

```xml
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct-jdk8</artifactId>
    <version>1.3.0.Final</version>
</dependency>

<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct-processor</artifactId>
    <version>1.3.0.Final</version>
    <scope>provided</scope>
</dependency>
```

添加 `mapstruct` 及 `lombok` 的插件依赖

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <annotationProcessorPaths>
            <path>
                <groupId>org.mapstruct</groupId>
                <artifactId>mapstruct-processor</artifactId>
                <version>1.3.0.Final</version>
            </path>
            <path>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <version>${lombok.version}</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>

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
       <groupId>org.mapstruct</groupId>
       <artifactId>mapstruct-jdk8</artifactId>
       <version>1.3.0.Final</version>
   </dependency>
   
   <dependency>
       <groupId>org.mapstruct</groupId>
       <artifactId>mapstruct-processor</artifactId>
       <version>1.3.0.Final</version>
       <scope>provided</scope>
   </dependency>


   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-test</artifactId>
       <scope>test</scope>
   </dependency>
</dependencies>
```

### 编码

1. 编写 BO 类，用于接收前端入参

```java
@Getter
@Setter
public class UserBO {

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    private String mobile;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Length(min = 6, max = 16, message = "密码长度必须在6-16位之间")
    private String upwd;

}
```

2. 编写 Mappering 转换器

 - 类上添加 `@Mapper` 注解，用于项目启动时自动加载
 - 方法上添加 `@Mapping` 注解，用于设置转换规则
 - 设置常量 `INSTANCE`，用于其它方法调用，也可以通过 Spring 的 IOC 进行注入

```java
@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "mobile", target = "name")
    User bo2Do(UserBO bo);

}
```

3. 编写 controller 接口

```java
@RestController
@RequestMapping("/user")
@Slf4j
@AllArgsConstructor
public class UserController {

    private UserRepository userRepository;

    @PostMapping("/register")
    public User register(@Valid @RequestBody UserBO userBO) {
        return userRepository.save(UserMapper.INSTANCE.bo2Do(userBO));
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
                .content("{\"mobile\":\"13700000001\",\"upwd\":\"123456\"}")
        )
        .andDo(MockMvcResultHandlers.print())
        .andReturn();

        Assert.assertEquals(200, mvcResult.getResponse().getStatus());
    }

}
```

### 源码地址

本章源码 : <https://gitee.com/gongm_24/spring-boot-tutorial.git>

### 总结

MapStruct 释放掉大量的属性复制的代码，改为编译时自动生成，所以只是精简了项目代码。
