package com.ithema.reggie;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBootTest
@RunWith(SpringRunner.class)
//@ContextConfiguration(value = "classpath:application.yml")
public class SS {



    @Autowired
    private RedisTemplate redisTemplate;
    @Test
    public void ss(){

        redisTemplate.opsForValue().set("s",1);
    }
}
