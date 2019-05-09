第三章：SpringBoot项目使用测试用例
---

### 课程目标

基于上一章，使用测试用例实现对增删查改接口的测试

### 操作步骤

#### 添加依赖

引入 `spring-boot-starter-test` 的依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

添加依赖后的整体 dependencies 如下所示

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
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
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

#### 编码

> 测试用例编写在 src/test/java 源目录下

1. 编写测试用例

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

 - 注解分析

```
@RunWith(SpringRunner.class) // 指定 SpringRunner 作为单元测试的执行类，SpringRunner 是 spring-test 提供的测试执行单元类
@WebAppConfiguration         // 模拟 ServletContext
@SpringBootTest(classes = Application.class) // 指定测试启动类，配置文件以及环境
```

 - MockMvc 用于向 controller 接口发起模拟请求
 - @Before 会在测试用例执行之前执行，在本例中用于初始化环境
 - @Test 标记当前方法是需要执行的测试用例

### 验证结果

选择测试用例，右键选择 Run

### 源码地址

本章源码 : <https://gitee.com/gongm_24/spring-boot-tutorial.git>

### 总结

请为自己的所有方法编写测试用例

### 参考

 - <https://blog.csdn.net/u010002184/article/details/81174153>
