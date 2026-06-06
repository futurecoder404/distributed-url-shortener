package com.sarvesh.distributedurlshortener.ratelimit;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RateLimitService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final long MAX_REQUESTS = 5;

    public boolean isAllowed(String ipAddress) {

        String key = "rate_limit:" + ipAddress;

        String currentCount =
                redisTemplate.opsForValue().get(key);

        if (currentCount == null) {

            redisTemplate.opsForValue()
                    .set(
                            key,
                            "1",
                            60,
                            TimeUnit.SECONDS
                    );

            return true;
        }

        long count = Long.parseLong(currentCount);

        if (count >= MAX_REQUESTS) {
            return false;
        }

        redisTemplate.opsForValue()
                .increment(key);

        return true;
    }
}