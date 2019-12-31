十、整合MapStruct优雅复制属性
---

在之前章节的例子中，我们在接收参数的时候都是使用的 User 类对象，这是一个 DO 对象，
但是这个对象与前端业务通常不能对等，所以更多时候，会创建一个 BO 类用来接收入参。
那么这时就会涉及到 BO 属性值复制至 DO，然后代码里就会出现大量的 `do.set(bo.get())`，
这时，对反射比较了解的同学应该就会尝试使用反射来解决这个问题，虽然可以解决问题，但是反射会降低程序性能，
而 MapStruct 则是另外一种解决方案。

### 相关知识
MapStruct官网：<http://mapstruct.org>

### 目标
整合 MapStruce 以及 Lombok

### 准备工作
 - Idea 集成开发环境

#### 安装 MapStruct 插件
File -> settings，打开 Idea 的设置界面，从左侧栏选择 Plugins 选项，再在右侧查询 MapStruce，点击安装。

#### 设置
File -> Settings 打开设置界面，
选择 Build,Execution,Deployment -> Compiler -> Annotation Processors 进入设置界面，
勾选 enable annotation processing

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

整体依赖如下
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
<build>
    <plugins>
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
    </plugins>
</build>
```

#### 编码
1. BO 层代码，用于接收前端入参
```java
@Getter
@Setter
public class UserBO {

    private String mobile;
    private String upwd;

}
```

2. MapStruct 接口
 - 类上添加 `@Mapper` 注解，项目编译时，MapStruct 将自动为该接口生成实现类
 - 方法上添加 `@Mapping` 注解，用于设置规则
 - 设置常量 `INSTANCE`，供其它类调用，也可以通过 Spring 的 IOC 进行注入
```java
@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "mobile", target = "name")
    User bo2Do(UserBO bo);

}
```

3. Controller 层代码
```java
@RestController
@Slf4j
@AllArgsConstructor
public class UserController {

    @PostMapping("/register")
    public User register(@RequestBody UserBO userBo) {
        User user = UserMapper.INSTANCE.bo2Do(userBo);
        // ... 执行数据库操作
        return user;
    }

}
```

4. 启动类
```java
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
```

### 验证结果
#### 测试用例
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

输出如下
```
{"id":null,"name":"13700000001","sex":null,"birthday":null,"level":null}
```

#### 编译项目
在项目的 target 子目录下可以找到 UserMapperImpl.class 文件，这个就是 MapStruct 在编译期自动生成实现类
```java
public class UserMapperImpl implements UserMapper {
    public UserMapperImpl() {
    }

    public User bo2Do(UserBO bo) {
        if (bo == null) {
            return null;
        } else {
            User user = new User();
            user.setName(bo.getMobile());
            return user;
        }
    }
}
```

### 源码地址

本章源码 : <https://gitee.com/gongm_24/spring-boot-tutorial.git>

### 结束语

MapStruct 释放掉大量的属性复制的代码，改为编译期自动生成，精简了项目代码同时也保证了高性能
