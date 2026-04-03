package com.technical.config.ratelimit;

import com.technical.commonutil.CommonUtil;
import com.technical.commonutil.UserUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

    public boolean isRateLimited(HttpServletRequest request) {
        if (!properties.isEnabled()) {
            return false;
        }

        String path = request.getRequestURI();
        String clientId = CommonUtil.getClientIP(request);
        String userName = UserUtil.getCurrentUsername();
        String key = String.format("rate_limit:%s:%s:%s", path, clientId, StringUtils.isEmpty(userName) ? "anonymous" : userName);

        // Get endpoint-specific config
        RateLimitProperties.EndpointConfig config = properties.getConfigForPath(path);
        if (config == null) {
            // No configuration found for this endpoint, allow the request
            return false;
        }

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
}