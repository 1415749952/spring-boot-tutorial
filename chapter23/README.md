二十三、整合Redis之集成缓存SpringDataCache
---

### 相关知识
#### 常用 Spring Cache 缓存注解

 - @CacheConfig 在类上设置当前缓存的一些公共设置，比如缓存名称。
 - @Cacheable   作用在方法上，表明该方法的结果可以缓存，如果缓存存在，则目标方法不会被调用，直接从缓存中获取，如果缓存不存在，则执行方法体，并将结果存入缓存。
 - @CacheEvice  作用在方法上，删除缓存项或者清空缓存。
 - @CachePut    作用在方法上，不管缓存是否存在，都会执行方法体，并将结果存入缓存。
 - @Caching     作用在方法上，以上的注解如果需要同时注解多个，可以包在 @Caching 内

### 目标
整合 Redis 实现对 redis 的增删查改

### 准备工作
#### 创建表
```mysql
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(32) NOT NULL COMMENT '用户名',
  `password` varchar(32) NOT NULL COMMENT '密码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='用户';
```

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

添加 `redis`、`jpa` 及 `mysql` 的依赖，添加后的整体依赖如下
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
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>

    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-pool2</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-cache</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
    </dependency>
</dependencies>
```

#### 配置
属性 `spring.cache.type` 用于配置缓存类型，默认为 `simple`，配置使用 redis 作为缓存中间件，只需要配置 `spring.cache.type` 属性为 `redis` 即可
```yaml
spring:
  cache:
    type: redis
  datasource:
    url: jdbc:mysql://49.235.247.175:3306/test
    driver-class-name: com.mysql.jdbc.Driver
    username: test
    password: 123456
  jpa:
    database: mysql
    # 显示后台处理的SQL语句
    show-sql: true
    # 自动检查实体和数据库表是否一致，如果不一致则会进行更新数据库表
    hibernate:
      ddl-auto: none
  redis:
    host: 49.235.247.175
    password: 123456
    # 连接超时时间（毫秒）
    timeout: 10000
    # Redis默认情况下有16个分片，这里配置具体使用的分片，默认是0
    database: 0
    lettuce:
      pool:
        # 连接池最大连接数（使用负值表示没有限制） 默认 8
        max-active: 8
        # 连接池最大阻塞等待时间（使用负值表示没有限制） 默认 -1
        max-wait: -1
        # 连接池中的最大空闲连接 默认 8
        max-idle: 8
        # 连接池中的最小空闲连接 默认 0
        min-idle: 0
```

#### 编码
##### 实体对象
因为 Redis 初始化时，默认使用的序列化类是 JdkSerializationRedisSerializer，所以需要实体对象实现 Serializable 接口。
```java
@Data
@Entity
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;

}
```

##### Repository 层代码
```java
public interface UserRepository extends JpaRepository<User, Long> {
}
```

##### Service 层代码
对增删查改方法添加缓存注解
```java
@CacheConfig(cacheNames = "user")
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Cacheable(key = "#id")
    public User getUser(Long id) {
        return userRepository.getOne(id);
    }

    @CachePut(key = "#user.id")
    public User addUser(User user) {
        userRepository.save(user);
        return user;
    }

    @CachePut(key = "#user.id")
    public User updateUser(User user) {
        userRepository.save(user);
        return user;
    }

    @CacheEvict(key = "#id")
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

}
```

##### 启动类
在启动类上添加 @EnableCaching 注解，用于开启缓存
```java
@EnableCaching
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
@Slf4j
@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = Application.class)
public class CacheTest {

    @Autowired
    private UserService userService;

    @Test
    public void testCache() {
        User user = new User();
        user.setUsername("gigi");
        user.setPassword("123456");
        log.info("执行数据新增，新增时会根据ID缓存用户对象");
        userService.addUser(user);
        log.info("执行数据加载，获取时将会直接从缓存中获取对象，而不用执行SQL");
        user = userService.getUser(user.getId());
        log.info("数据：{}", user);
    }

}
```

将 UserService 中的缓存相关注解全部注释，执行测试用例，日志显示如下：
```
执行数据新增，新增时会根据ID缓存用户对象
Hibernate: insert into user (password, username) values (?, ?)
执行数据加载，获取时将会直接从缓存中获取对象，而不用执行SQL
Hibernate: select user0_.id as id1_0_0_, user0_.password as password2_0_0_, user0_.username as username3_0_0_ from user user0_ where user0_.id=?
数据：User(id=8, username=gigi, password=123456)
```

恢复缓存注解，再次执行测试用例，日志显示如下：
```
执行数据新增，新增时会根据ID缓存用户对象
Hibernate: insert into user (password, username) values (?, ?)
执行数据加载，获取时将会直接从缓存中获取对象，而不用执行SQL
数据：User(id=10, username=gigi, password=123456)
```

可以看到，使用缓存之后，根据用户ID获取用户数据时，并不执行SQL。

去到 Redis 进行查看，可以看到当前的 Redis Key 为 `user::12`，其中 12 为用户的 ID

### 源码地址

本章源码 : <https://gitee.com/gongm_24/spring-boot-tutorial.git>

### 结束语
数据库一直都是系统高性能的一个瓶颈，合理正确地使用缓存，可以大大提升系统性能。
但是随之而来的是系统复杂度提高，有更多的问题需要处理，比如缓存一致性，缓存穿透，缓存雪崩等。

### 扩展
#### 根据条件操作缓存
根据条件操作缓存内容并不影响数据库操作，条件表达式返回一个布尔值，true/false，当条件为true，则进行缓存操作，否则直接调用方法执行的返回结果。
 - 长度： @CachePut(value = "user", key = "#user.id",condition = "#user.username.length() < 10") 只缓存用户名长度少于10的数据
 - 大小： @Cacheable(value = "user", key = "#id",condition = "#id < 10") 只缓存ID小于10的数据
 - 组合： @Cacheable(value="user",key="#user.username.concat(##user.password)")
 - 提前操作： @CacheEvict(value="user",allEntries=true,beforeInvocation=true) 加上beforeInvocation=true后，不管内部是否报错，缓存都将被清除，默认情况为false