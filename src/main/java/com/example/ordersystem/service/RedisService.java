package com.example.ordersystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    public void setStockQuantityInRedis(String key, Integer value) {
        redisTemplate.opsForValue().setIfAbsent(key, value.toString());
    }

    public String getValue(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    public void incrementStockQuantityInRedis(String key, Integer quantity) {
        redisTemplate.opsForValue().increment(key, quantity);
    }


    public Integer decrementStockQuantityInRedis(String key, Integer quantity) {
        String script =
                "local currentStock = redis.call('GET', KEYS[1])\n" +
                        "if currentStock == false then\n" +
                        "    return -1\n" +
                        "end\n" +
                        "currentStock = tonumber(currentStock)\n" +
                        "local orderQuantity = tonumber(ARGV[1])\n" +
                        "if currentStock < orderQuantity then\n" +
                        "    return -2\n" +
                        "end\n" +
                        "local newStock = currentStock - orderQuantity\n" +
                        "redis.call('SET', KEYS[1], tostring(newStock))\n" +
                        "return newStock";

        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(script);
        redisScript.setResultType(Long.class);

        Integer result = Math.toIntExact(redisTemplate.execute(
                redisScript,
                Collections.singletonList(key),
                quantity.toString()
        ));

        if (result == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Redis 작업 실패");
        }

        if (result == -1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 상품의 재고가 존재하지 않습니다.");
        }

        if (result == -2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "재고가 부족합니다.");
        }

        return result;
    }

}
