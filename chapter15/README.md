十五、使用AOP记录操作日志
---

### 相关知识
AOP 即面对切面编程，是 Spring 框架的两大核心特性之一。

#### 相关概念
 - 切面（Aspect）：一个关注点的模块化，这个关注点可能会横切多个对象。事务管理是J2EE应用中一个关于横切关注点的很好的例子。在Spring AOP中，切面可以使用基于模式或者基于@Aspect注解的方式来实现。
 - 连接点（Joinpoint）：在程序执行过程中某个特定的点，比如某方法调用的时候或者处理异常的时候。在Spring AOP中，一个连接点总是表示一个方法的执行。
 - 通知（Advice）：在切面的某个特定的连接点上执行的动作。其中包括了“around”、“before”和“after”等不同类型的通知（通知的类型将在后面部分进行讨论）。许多AOP框架（包括Spring）都是以拦截器做通知模型，并维护一个以连接点为中心的拦截器链。
 - 切入点（Pointcut）：匹配连接点的断言。通知和一个切入点表达式关联，并在满足这个切入点的连接点上运行（例如，当执行某个特定名称的方法时）。切入点表达式如何和连接点匹配是AOP的核心：Spring缺省使用AspectJ切入点语法。
 - 引入（Introduction）：用来给一个类型声明额外的方法或属性（也被称为连接类型声明（inter-type declaration））。Spring允许引入新的接口（以及一个对应的实现）到任何被代理的对象。例如，你可以使用引入来使一个bean实现IsModified接口，以便简化缓存机制。
 - 目标对象（Target Object）：被一个或者多个切面所通知的对象。也被称做被通知（advised）对象。既然Spring AOP是通过运行时代理实现的，这个对象永远是一个被代理（proxied）对象。
 - AOP代理（AOP Proxy）：AOP框架创建的对象，用来实现切面契约（例如通知方法执行等等）。在Spring中，AOP代理可以是JDK动态代理或者CGLIB代理。
 - 织入（Weaving）：把切面连接到其它的应用程序类型或者对象上，并创建一个被通知的对象。这些可以在编译时（例如使用AspectJ编译器），类加载时和运行时完成。Spring和其他纯Java AOP框架一样，在运行时完成织入。

### 目标
通过 AOP 的方式，监控客户端对Controller方法的操作，记录操作日志。

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

添加 `spring-boot-starter-aop` 的依赖
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
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
        <artifactId>spring-boot-starter-aop</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

#### 编码
准备一个自定义注解SysLog，对需要监控的方法进行标记
```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysLog {

    String value() default "操作日志";

}
```

编写 Controller 方法，并对该方法标注上 @SysLog 注解，注解内容为该方法的功能描述
```java
@RestController
public class IndexController {

    @SysLog("用户注册")
    @PostMapping("/register")
    public UserBO register(@RequestBody UserBO userBo) {
        return userBo;
    }
    
}
```

定义切面类

一个普通类变成切面类需要两个步骤
1. 在类上注解 @Component，把类加入至 IOC 容器
2. 在类上注解 @Aspect，使之成为切面类

定义切入点

在切面类中添加一个普通方法，在方法上加注解 @Pointcut 用来定义切入点，@Pointcut 的内容是一个表达式，在[扩展](#ext)部分有更详细介绍

定义通知

 - @Before：前置通知：在某连接点之前执行的通知，但这个通知不能阻止连接点之前的执行流程（除非它抛出一个异常）。
 - @AfterReturning ：后置通知：在某连接点正常完成后执行的通知，通常在一个匹配的方法返回的时候执行。
 - @AfterThrowing:异常通知：在方法抛出异常退出时执行的通知。　　　　　　　
 - @After 最终通知。当某连接点退出的时候执行的通知（不论是正常返回还是异常退出）。
 - @Around：环绕通知：包围一个连接点的通知，如方法调用。这是最强大的一种通知类型。环绕通知可以在方法调用前后完成自定义的行为。它也会选择是否继续执行连接点或直接返回它自己的返回值或抛出异常来结束执行。   

> 任何通知方法可以将第一个参数定义为org.aspectj.lang.JoinPoint类型（环绕通知需要定义第一个参数为ProceedingJoinPoint类型，它是 JoinPoint 的一个子类）。JoinPoint接口提供了一系列有用的方法，比如 getArgs()（返回方法参数）、getThis()（返回代理对象）、getTarget()（返回目标）、getSignature()（返回正在被通知的方法相关信息）和 toString()（打印出正在被通知的方法的有用信息）

```java
@Slf4j
@AllArgsConstructor
@Aspect
@Component
public class SysLogAspect {

    private ObjectMapper objectMapper;
    /**
     * 定义切入点，切入点为 标注了 @SysLog 注解的所有方法
     */
    @Pointcut("@annotation(com.mhkj.annotation.SysLog)")
    public void logPointCut() {
    }
    /**
     * 环绕型
     */
    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long st = System.currentTimeMillis();
        // 执行方法
        Object result = point.proceed();
        // 执行时长(毫秒)
        long time = System.currentTimeMillis() - st;
        // 打印日志
        printSysLog(point, time);
        return result;
    }

    private void printSysLog(ProceedingJoinPoint point, long time) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        try {
            SysLog syslog = method.getAnnotation(SysLog.class);
            String className = point.getTarget().getClass().getName();
            String methodName = signature.getName();
            Object[] args = point.getArgs();
            log.info("操作：{}", syslog.value());
            log.info("请求方法：{}", className + "." + methodName + "()");
            log.info("请求参数：{}", objectMapper.writeValueAsString(args));
            log.info("请求时间：{}", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            log.info    ("请求耗时：{}", time);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}
```

编写启动类

启动类无需添加 @EnableAspectJAutoProxy 注解，只要添加了 spring-boot-starter-aop 的依赖，就已经默认开启了 AOP 功能
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

执行后查看日志，可以看到以下信息
```
操作：用户注册
请求方法：com.mhkj.controller.IndexController.register()
请求参数：[{"mobile":null,"upwd":"123456","sex":null}]
请求时间：2019-12-31T02:14:47.65
请求耗时：5
```

### 源码地址

本章源码 : <https://gitee.com/gongm_24/spring-boot-tutorial.git>

### 参考
 - <https://www.cnblogs.com/lic309/p/4079194.html>
 - <https://blog.csdn.net/lmb55/article/details/82470388>

### 总结

AOP 作为 Spring 框架两个最重要特性之一，在框架层面应用居多。
本例实现了对请求进行日志输出，也可以结合之前的课程，将日志记录至数据库。

### 扩展

#### <span id="ext">PointCut 表达式</span>
 - execution
 
匹配子表达式，格式为
```
execution([可见性] 返回类型 [声明类型].方法名(参数) [异常])
```
表达式支持通配符：
> *：匹配所有字符<br>
> ..：一般用于匹配多个包，多个参数<br>
> +：表示类及其子类

表达式支持运算符：&&、||、!

例:
```
// 匹配 com.cjm.model 包及其子包中所有类中的所有方法，返回类型任意，方法参数任意
@Pointcut("execution(* com.cjm.model..*.*(..))")
```
 - @annotation
 
匹配连接点被它参数指定的Annotation注解的方法
```
// 匹配所有被@AdviceAnnotation标注的方法
@Pointcut("@annotation(com.cjm.annotation.AdviceAnnotation)")
```
 - within
 
匹配连接点所在的Java类或者包
```
// 匹配Person类中的所有方法
@Pointcut("within(com.cjm.model.Person)")
// 匹配com.cjm包及其子包中所有类中的所有方法
@Pointcut("within(com.cjm..*)")
```
 - @within
 
匹配在类一级使用了参数确定的注解的类，其所有方法都将被匹配
```
// 匹配所有被@AdviceAnnotation标注的类
@Pointcut("@within(com.cjm.annotation.AdviceAnnotation)") 
```
 - @target

和@within的功能类似，但必须要指定注解接口的保留策略为RUNTIME
```
@Pointcut("@target(com.cjm.annotation.AdviceAnnotation)")
```
 - bean
 
通过受管Bean的名字来限定连接点所在的Bean
```
// 匹配名称为person的Bean
@Pointcut("bean(person)")
```
 - this
向通知方法中传入代理对象的引用  
```
@Before("pointCut() && this(proxy)")
public void beforeAdvide(JoinPoint point, Object proxy){
    //处理逻辑
}
```
 - target
 
向通知方法中传入目标对象的引用
```
@Before("pointCut() && target(target)
public void beforeAdvide(JoinPoint point, Object proxy){
    //处理逻辑
}
```
 - args

将参数传入到通知方法中
```
@Before("pointCut() && args(age,username)")
public void beforeAdvide(JoinPoint point, int age, String username){
    //处理逻辑
}
```
 - @args

传入连接点的对象对应的Java类必须被@args指定的Annotation注解标注
```
@Before("@args(com.cjm.annotation.AdviceAnnotation)")
public void beforeAdvide(JoinPoint point){
    //处理逻辑
}
```