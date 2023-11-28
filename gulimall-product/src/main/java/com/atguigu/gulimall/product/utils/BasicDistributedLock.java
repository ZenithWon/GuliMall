package com.atguigu.gulimall.product.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Component
public class BasicDistributedLock implements ILock{
    @Autowired
    StringRedisTemplate redisTemplate;

    private static final String delLockScript="if redis.call('get',KEYS[1])==ARGV[1] " +
                                            "then return redis.call('del',KEYS[1]) " +
                                            "else return 0 end";

    @Override
    public Boolean tryLock(String key) {
        long id = Thread.currentThread().getId();
        String value="Thread:"+id;

        Boolean res = redisTemplate.opsForValue().setIfAbsent(key , value , 30 , TimeUnit.SECONDS);
        return res;
    }

    @Override
    public void delLock(String key) {
        long id = Thread.currentThread().getId();
        String value="Thread:"+id;
        Long res = redisTemplate.execute(new DefaultRedisScript<Long>(delLockScript , Long.class) , Collections.singletonList(key) , value);
    }
}
