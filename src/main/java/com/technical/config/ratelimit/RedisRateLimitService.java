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

        log.info("key: {}, path: {}", key, path);

        // Get endpoint-specific config or use defaults
        RateLimitProperties.EndpointConfig config = properties.getConfigForPath(path)
                .orElseGet(() -> new RateLimitProperties.EndpointConfig() {{
                    setLimit(properties.getDf().getLimit());
                    setWindow(properties.getDf().getWindow());
                }});

        log.info("config: {}", config);

        String rateLimiterKey = String.format("rate_limit:%s", key);
        ValueOperations<String, Integer> valueOps = redisTemplate.opsForValue();

        Integer currentUsage = valueOps.get(rateLimiterKey);
        if (currentUsage == null) {
            currentUsage = 0;
        }

        log.info("rateLimiterKey = {}, currentUsage = {}, limit = {}, window = {}",
                rateLimiterKey, currentUsage, config.getLimit(), config.getWindow());

        if (currentUsage < config.getLimit()) {
            valueOps.set(rateLimiterKey, currentUsage + 1,
                    config.getWindow(), TimeUnit.SECONDS);
            return false;
        }
        return true;
    }

    public boolean shouldRateLimit(String path) {
        boolean result = properties.isEnabled() && properties.isPathIncluded(path);
        log.info(result? "{} was enabled the rate limit": "{} was disabled the rate limit", path);
        return result;
    }
}