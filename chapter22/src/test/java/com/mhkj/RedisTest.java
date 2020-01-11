package com.mhkj;

import com.mhkj.lock.RedisLock;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

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
            log.info("方法[ {} ]释放锁成功", batch);
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
