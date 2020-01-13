二十四、整合SpringSecurity之最简登录及方法鉴权
---

### 目标
整合 SpringSecurity 实现登陆鉴权

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

添加 `springSecurity` 的依赖
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
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
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>

    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <scope>provided</scope>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
</dependencies>
```

#### 编码
##### Controller 层
添加一个最简单的测试接口
```java
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "hello world";
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

可以看到，除了在 pom 文件加了一个 `spring-boot-starter-security` 依赖，其它跟创建最简 springBoot 应用没有区别。

#### 最简初体验
最简体验就是什么都不做，直接体验，启动应用，查看日志，可以看到有一行比较特殊
```
Using generated security password: 87f53063-eb7a-4708-80d5-e804df04b8a0
```

通过网页访问 http://localhost:8080/hello 地址，原本页面应该显示 `hello world` 字样，但实际有所偏差，
页面被自动跳转至 `http://localhost:8080/login`，而且出现一个很丑的登录界面，这个不重要，重要的是我们的访问被阻止了，被 SpringSecurity 要求登录。

SpringSecurity 默认提供了一个用户名称叫 `user`，密码就是上面提到的日志里打印出来的，从格式看应该是一个 UUID。
输入用户名密码进行登录，我们终于看到了 `hello world`，这就是 SpringSecurity 最简初体验。

#### 配置用户名密码
##### 配置
配置用户名，密码，角色，配置过后，重启应用，日志中将不会再有生成密码那一行。
```yaml
spring:
  security:
    user:
      name: app
      password: 123456
      roles: USER
```

#### 添加方法权限验证
##### 注册
在启动类上添加 @EnableGlobalMethodSecurity 注解，用于开启方法权限验证
 - securedEnabled：开启 @Secured 注解
    - 单个角色：@Secured("ROLE_USER")
    - 多个角色任意一个：@Secured({"ROLE_USER","ROLE_ADMIN"})
 - prePostEnabled：开启 @PreAuthorize 及 @PostAuthorize 注解，分别适用于进入方法前后进行鉴权，支持表达式
    - 允许所有访问：@PreAuthorize("true")
    - 拒绝所有访问：@PreAuthorize("false")
    - 单个角色：@PreAuthorize("hasRole('ROLE_USER')")
    - 多个角色与条件：@PreAuthorize("hasRole('ROLE_USER') AND hasRole('ROLE_ADMIN')")
    - 多个角色或条件：@PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_ADMIN')")
 - jsr250Enabled：开启 JSR-250 相关注解
    - 允许所有访问：@PermitAll
    - 拒绝所有访问：@DenyAll
    - 多个角色任意一个：@RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})

```java
@SpringBootApplication
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true, jsr250Enabled = true)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
```

##### 验证
编写 Service 并为每个方法添加权限注解
```java
@Service
public class SecurityService {

	@Secured("ROLE_USER")
	public String secure() {
		return "Hello Security";
	}

	@PreAuthorize("true")
	public String authorized() {
		return "Hello World";
	}

	@PreAuthorize("false")
	public String denied() {
		return "Goodbye World";
	}

}
```

执行测试用例
```java
@Slf4j
@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = Application.class)
public class SecurityTest {

    @Autowired
    private SecurityService securityService;

    private Authentication authentication;

    @Before
    public void init() {
        this.authentication = new UsernamePasswordAuthenticationToken("app","123456");
    }

    @After
    public void close() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void secure() {
        assertThatExceptionOfType(AuthenticationException.class)
                .isThrownBy(() -> this.securityService.secure());
    }

    @Test
    public void authenticated() {
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        assertThat("Hello Security").isEqualTo(this.securityService.secure());
    }

    @Test
    public void preauth() {
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        assertThat("Hello World").isEqualTo(this.securityService.authorized());
    }

    @Test
    public void denied() {
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        assertThatExceptionOfType(AccessDeniedException.class)
                .isThrownBy(() -> this.securityService.denied());
    }

}
```

#### 使用编码的方式进行配置
SpringSecurity 提供了一个 WebSecurityConfigurerAdapter 配置类，此类有三个重要的方法可供继承
 - configure(AuthenticationManagerBuilder auth)配置在内存中进行注册公开内存的身份验证
 - configure(WebSecurity web)配置拦截资源，例如过滤掉css/js/images等静态资源
 - configure(HttpSecurity http)定义需要拦截的URL
 
本例，我们重写 configure(AuthenticationManagerBuilder auth) 方法，在内存中添加两个用户。
> 使用了编码的方式进行配置后，在配置文件中配置的用户将失效。
```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        auth.inMemoryAuthentication().passwordEncoder(passwordEncoder)
                .withUser("user")
                    .password(passwordEncoder.encode("123"))
                    .roles("USER")
                .and()
                .withUser("admin")
                    .password(passwordEncoder.encode("123"))
                    .roles("USER", "ADMIN");
    }

}
```

### 源码地址

本章源码 : <https://gitee.com/gongm_24/spring-boot-tutorial.git>

### 结束语
SpringSecurity 与 Shiro 是两个最常用的权限框架，SpringSecurity 因为是 Spring 全家桶中的一员，所以在与 Spring 的集成方面会更好一点。
