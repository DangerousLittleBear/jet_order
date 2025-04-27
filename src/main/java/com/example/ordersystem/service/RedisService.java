package com.example.ordersystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    public void setStockQuantityInRedis(String key, Integer value) {
        redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    public String getValue(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }
}
