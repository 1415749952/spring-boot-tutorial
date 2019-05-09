第七章：SpringBoot集成Lombok让项目更简洁
---

在常规的实体类中，我们会对该类中所有字段生成 get / set 方法，遇到字段比较多的，这些方法会占用大量代码篇幅。
Lombok 就是为了简化此类代码。

### 相关知识

Lombok官网：<https://projectlombok.org/>

#### 常用注解

一、@Setter | @Getter

提供无参构造方法以及 getter、setter 方法

```
@Getter
@Setter
public class User1 {

    private Long id;

    private String username;

}
```

二、@ToString

提供无参构造方法以及 toString 方法

 - includeFieldNames 是否包含属性名
 - exclude 排除指定属性
 - callSuper 是否包含父类属性

```
@ToString
public class User2 {

    private Long id;

    private String username;

}
```

三、@EqualsAndHashCode

提供无参构造方法以及 equals、hashCode 方法

```
@EqualsAndHashCode
public class User3 {

    private Long id;

    private String username;

}
```

四、@AllArgsConstructor

提供一个全参数的构造方法，默认不提供无参构造

```
@AllArgsConstructor
public class User4 {

    private Long id;

    private String username;

}
```

五、@NoArgsConstructor

提供一个无参构造

```
@NoArgsConstructor
public class User5 {

    private Long id;

    private String username;

}
```

六、@Data

结合了@ToString，@EqualsAndHashCode，@Getter、@Setter、@NoArgsConstructor

- staticConstructor 生成静态工厂方法的方法名，如果设置了该参数，则生成的无参构造方法将被置为私有的。

```
@Data
public class User4 {

    private Long id;

    private String username;

}
```

七、@Slf4j

提供 org.slf4j.Logger 变量，变量名为 log

```
@Slf4j
@RestController
public class UserController {

    @GetMapping("/listUser")
    public List<User1> listUser() {
        log.error("log with lombok");
        return null;
    }

}
```

### 课程目标

本章将在[SpringBoot集成SpringBootJPA完成CURD]()的基础上，整合 lombok，实现对数据库表的操作。
以及熟悉并学会使用 lombok，简化项目代码

### 操作步骤

本文使用 Idea 集成开发环境

#### 环境准备

安装 lombok 插件

File -> settings，打开 Idea 的设置界面，从左侧栏选择 Plugins 选项，再在右侧查询 lombok，点击安装。

#### 添加依赖

引入 Spring Boot Starter 父工程

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.0.5.RELEASE</version>
</parent>
```

添加 `lombok` 的依赖

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <scope>provided</scope>
</dependency>
```

同时添加对 `spring-boot-starter-test` 的依赖，用于进行单元测试，完整依赖如下

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

改写实体类

将 User 类的 get / set 方法全部删除，在类上添加注解 @Data，修改后的 User 类代码如下，可以看到整个类的代码变得非常清爽。

```java
@Data
@Entity
public class User {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Integer sex;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

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

    @Autowired
    private UserRepository userRepository;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testInsert() throws Exception {
        MvcResult mvcResult = mvc.perform(
                MockMvcRequestBuilders
                .post("/user/add")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content("{\"name\":\"user1\",\"sex\":1,\"birthday\":\"2000-05-21\"}")
        )
//        .andExpect(status().isOk());
//        .andExpect(content().string("hello"))
        .andDo(MockMvcResultHandlers.print())
        .andReturn();

        Assert.assertEquals(200, mvcResult.getResponse().getStatus());
    }

}
```

可以看到，改变后的实体类跟改变前的实体可以达到同样的效果。

### 源码地址

本章源码 : <https://gitee.com/gongm_24/spring-boot-tutorial.git>

### 总结

Lombok 只是为了减少一些无关紧要的代码，减少编码的工作量，并不会减少程序的复杂度，这部分代码会在项目编译期间自动生成。