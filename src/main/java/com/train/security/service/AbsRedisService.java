package com.train.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public abstract class AbsRedisService {

    private final RedisTemplate<String,Object> redisTemplate;

    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void setWithExp(String key, Object value, long exp,TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key , value, exp, timeUnit);
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    public Boolean isKeyExist(String key) {
        return redisTemplate.hasKey(key);
    }
}
