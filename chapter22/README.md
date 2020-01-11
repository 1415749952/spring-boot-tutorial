二十一、整合Redis之实现分布式锁
---

### 相关知识
#### 分布式锁
分布式锁是控制分布式系统之间同步访问共享资源的一种方式，
在分布式系统中，如果不同的应用之间共享一个或一组资源，那么访问这些资源的时候，
往往需要互斥来防止彼此干扰来保证一致性，在这种情况下，便需要使用到分布式锁。
 - 互斥性。在分布式环境下，同一时间只有一个客户端能持有锁。
 - 具备锁失效机制，防止死锁。例如锁的持有者在持有锁期间崩溃而没有主动解锁，锁需要在规定时间后自动失效，以保证后续可用。
 - 具备可重入性，防止死锁。
 - 解铃还须系铃人。释放锁与加锁应该为相同客户端，不能把别人加的锁给解了。

#### Redis 实现分布式锁原理
实现原理可参考 <http://www.redis.cn/topics/distlock.html>。

Redis 是单线程的，所以 Redis 命令具有原子性，Redis 提供了以下几个命令
 - setnx，意思是待创建的键如果已存在，则创建失败，否则创建成功，实现互斥性。
 - expire，给键加上过期时间，实现自动失效机制
 - set key value \[EX|PX\] \[NX\]，可以将上面两个命令的动作，在这一个命令实现，其中，EX|PX 是过期时间，EX 使用秒，PX 使用毫秒，NX 表示 setnx 的意思

因为锁的实现中拥有比较、加锁等一系列操作，为保证原子性，需要引入 Lua 脚本

加锁流程：
```mermaid 
flowchat
st=>start: 开始加锁
e=>end: 加锁成功
e3=>end: 加锁失败
e2=>end: 加锁失败
cond1=>condition: 是否存在锁
cond2=>condition: 锁的拥有者是否是自己
op2=>operation: 尝试加锁
cond3=>condition: 尝试是否成功

st->cond1
cond1(yes)->cond2
cond1(no)->op2(right)->cond3
cond2(yes, bottom)->e
cond2(no, left)->e2
cond3(yes, bottom)->e
cond3(no)->e3
```

释放锁流程
```mermaid 
flowchat
st=>start: 开始释放加锁
e=>end: 释放成功
e2=>end: 释放失败
e3=>end: 放弃
cond1=>condition: 是否存在锁
cond2=>condition: 锁的拥有者是否是自己
op1=>operation: 执行锁释放

st->cond1
cond1(yes)->cond2
cond1(no)->e3
cond2(yes, bottom)->op1->e
cond2(no, left)->e2
```

### 目标
整合 Redis 实现分布式锁

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
实现一个分布式锁，封装数据及实现
 - key：业务键
 - redisKey：Redis存储的键，在业务键上增加一个前缀，等于增加一个命名空间的意思
 - value：键值，创建时使用UUID生成，释放时使用该值进行校验操作人身份
 - expire 及 unit：过期时间
```java
public class RedisLock {
    // 锁默认前缀
    private static final String DEFAULT_LOCK_PREFIX = "LOCK:";
    // 锁默认过期时间，默认 5 分钟
    private static final long DEFAULT_EXPIRE = 5L;
    // 锁默认过期时间单位
    private static final TimeUnit DEFAULT_UNIT = TimeUnit.SECONDS;
    // 与 Redis 进行交互
    private RedisTemplate redisTemplate;
    // 键
    private String key;
    // 值
    private String value;
    private long expire;
    private TimeUnit unit;
    @Getter
    private String redisKey;

    private RedisLock() {}

    public static RedisLock newLock(RedisTemplate redisTemplate, String key) {
        return newLock(redisTemplate, key, DEFAULT_EXPIRE, DEFAULT_UNIT);
    }

    public static RedisLock newLock(RedisTemplate redisTemplate, String key, long expire, TimeUnit unit) {
        RedisLock lock = new RedisLock();
        lock.redisTemplate = redisTemplate;
        lock.key = key;
        lock.redisKey = DEFAULT_LOCK_PREFIX + key;
        lock.expire = expire;
        lock.unit = unit;
        lock.value = UUID.randomUUID().toString();
        return lock;
    }

    public boolean tryLock() {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return 'OK' " +
                "else return redis.call('set', KEYS[1], ARGV[1],'EX',ARGV[2],'NX') end";
        String result = (String) redisTemplate.execute(RedisScript.of(script, String.class),
                Collections.singletonList(getRedisKey()), this.value, String.valueOf(this.unit.toSeconds(this.expire)));
        return "OK".equals(result);
    }

    public boolean release() {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then redis.call('del', KEYS[1]) return 1 else return 0 end";
        Long result = (Long) redisTemplate.execute(RedisScript.of(script, Long.class),
                Collections.singletonList(getRedisKey()), this.value);
        return result != null && result > 0;
    }

}
```

### 验证
分布式锁，需要分布式环境，所以本例中只是简单模拟，创建多个测试用例，每一个用例相当于一个应用程序，同时启动多个用例进行加锁操作，最终只会有一个加锁成功。
```java
@Slf4j
@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = Application.class)
public class RedisTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private void testLock(String batch) throws Exception {
        RedisLock lock = RedisLock.newLock(stringRedisTemplate, "testRedisLock");
        try {
            if (lock.tryLock()) {
                log.info("方法[ {} ]加锁成功", batch);
                Thread.sleep(15000L);
            } else {
                log.info("方法[ {} ]加锁失败", batch);
            }
        } finally {
            lock.release();
            log.info("方法[ {} ]释放锁", batch);
        }
    }

    @Test
    public void testLock1() throws Exception {
        testLock("1");
    }

    @Test
    public void testLock2() throws Exception {
        testLock("2");
    }

}
```

### 源码地址

本章源码 : <https://gitee.com/gongm_24/spring-boot-tutorial.git>

### 参考
 - <https://blog.52itstyle.vip/archives/1264/>

### 结束语
分布式锁有很多实现方式，Redis 只是其中一种

### 扩展
#### Redis 相关资料
spring-data-redis文档： <https://docs.spring.io/spring-data/redis/docs/2.0.1.RELEASE/reference/html/#new-in-2.0.0>
Redis 文档： <https://redis.io/documentation>
Redis 中文文档： <http://www.redis.cn/commands.html>