package com.mhkj;

import com.mhkj.entity.User;
import com.mhkj.service.UserService;
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
