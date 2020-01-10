二十一、整合Redis
---

### 相关知识
#### Redis 简介
Redis 是一个开源的，基于内存中的，高性能的数据存储系统，它可以用作数据库、缓存和消息中间件。
Redis 支持多种类型的数据结构，如：string、hashes、lists、sets、sortedSets等。
Redis 内置了复制(replication)、LUA脚本(Lua scripting)、事务(transactions)、磁盘持久化(persistence)、LRU驱动事件(LRU eviction)等功能。
Redis 可以通过哨兵(Sentinel)以及集群(Cluster)提供高可用性

#### Lettuce 和 Jedis
Lettuce 和 Jedis 都是连接 Redis Server 的客户端程序，
SpringBoot2.x 之前默认使用 Jedis 作为与 Redis 进行交互的组件，SpringBoot2.x 则换成了 Lettuce（生菜）。
Jedis 在实现上是直连 redis server，多线程环境下非线程安全，除非使用连接池，为每个 Jedis 实例增加物理连接。
Lettuce 基于 Netty 的连接实例（StatefulRedisConnection），可以在多个线程间并发访问，且线程安全，满足多线程环境下的并发访问，
同时它是可伸缩的设计，一个连接实例不够的情况也可以按需增加连接实例。

### 目标
整合 Redis 实现对 redis 的增删查改

### 准备工作
#### 安装 Redis
介绍使用 Docker 方式安装，Docker 安装可以参考 <https://blog.csdn.net/gongm24/article/details/86357866>
##### 下载镜像
```
docker pull redis
```

##### 运行镜像
```
docker run --name redis \
    -p 6379:6379 \
    -itd redis --requirepass "123456"
```

##### 检查是否安装成功，使用客户端登入容器，执行 `redis-cli -a 123456` 命令，进行连接
```
docker exec -it redis /bin/bash
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

添加 `spring-boot-starter-data-redis` 的依赖
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-pool2</artifactId>
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
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```
#### 配置
```yaml
spring:
  redis:
    host: 127.0.0.1
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
```java
@Slf4j
@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = Application.class)
public class RedisTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void testString() throws Exception {
        stringRedisTemplate.opsForValue().set("welcome", "hello redis");
        String welcome = stringRedisTemplate.opsForValue().get("welcome");
        log.info("存储字符串: {}", welcome);
    }

}
```

#### 编码（使用自定义序列化）
##### 定义对象
@NoArgsConstructor 一定要记得加，反序列化时会调用无参构造函数进行对象实例化。
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    String username;
    String password;

}
```

##### 注册自定义 RedisTemplate
值的序列化选择了 jackson
```java
@Configuration
@AutoConfigureAfter(value = RedisAutoConfiguration.class)
public class CustomConfig {

    @Bean(name = "jsonRedisTemplate")
    public RedisTemplate<String, Object> jsonRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

}
```

##### 测试
```java
@Slf4j
@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = Application.class)
public class RedisTest {

    @Autowired
    @Qualifier("jsonRedisTemplate")
    private RedisTemplate<String, Object> jsonRedisTemplate;

    @Test
    public void testCustomerSerializer() throws Exception {
        jsonRedisTemplate.opsForValue().set("user:1", new User("lili", "123456"));
        User user = (User) jsonRedisTemplate.opsForValue().get("user:1");
        log.info("存储对象: {}", user);
    }

}
```

### 源码地址

本章源码 : <https://gitee.com/gongm_24/spring-boot-tutorial.git>

### 参考
 - <https://blog.52itstyle.vip/archives/1264/>

### 结束语
下列的就是Redis其它类型所对应的操作方式
 - opsForValue： 对应 String（字符串）
 - opsForZSet： 对应 ZSet（有序集合）
 - opsForHash： 对应 Hash（哈希）
 - opsForList： 对应 List（列表）
 - opsForSet： 对应 Set（集合）
 - opsForGeo： 对应 GEO（地理位置）

### 扩展
#### Redis 相关资料
spring-data-redis文档： <https://docs.spring.io/spring-data/redis/docs/2.0.1.RELEASE/reference/html/#new-in-2.0.0>
Redis 文档： <https://redis.io/documentation>
Redis 中文文档： <http://www.redis.cn/commands.html>