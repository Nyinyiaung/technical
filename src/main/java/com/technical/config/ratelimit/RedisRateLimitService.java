package com.technical.config.ratelimit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisRateLimitService {

    private final RedisTemplate<String, Integer> redisTemplate;
    private final RateLimitProperties properties;

    public boolean isRateLimited(String key, String path) {
        if (!properties.isEnabled()) {
            return false;
        }

        // Get endpoint-specific config or use defaults
        RateLimitProperties.EndpointConfig config = properties.getConfigForPath(path)
                .orElseGet(() -> new RateLimitProperties.EndpointConfig() {{
                    setLimit(properties.getDf().getLimit());
                    setWindow(properties.getDf().getWindow());
                }});

        log.info("config: {}", config);

        ValueOperations<String, Integer> valueOps = redisTemplate.opsForValue();

        // Atomically increment the counter
        Long current = valueOps.increment(key, 1);

        log.info("key: {}, current: {}", key, current);

        // If this is the first request, set the expiration
        if (current != null && current == 1) {
            redisTemplate.expire(key, config.getWindow(), TimeUnit.SECONDS);
        }

        // Check if rate limit is exceeded
        if (current != null && current > config.getLimit()) {
            log.warn("Rate limit exceeded for key: {}, current: {}, limit: {}", key, current, config.getLimit());
            return true;
        }

        return false;
    }

    public boolean shouldRateLimit(String path) {
        boolean result = properties.isEnabled() && properties.isPathIncluded(path);
        return result;
    }
}