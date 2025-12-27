package com.technical.config.ratelimit;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@EnableConfigurationProperties(RateLimitProperties.class)
@Import(com.technical.config.redis.RedisConfig.class)
@PropertySource("classpath:ratelimit.properties")
@ConfigurationPropertiesScan("com.technical.config.ratelimit")
public class RateLimitAutoConfiguration {

    @Bean
    @ConditionalOnProperty(name = "rate.limit.enabled", havingValue = "true", matchIfMissing = true)
    public RedisRateLimitService redisRateLimitService(
            RedisTemplate<String, Integer> rateLimitRedisTemplate,
            RateLimitProperties properties) {
        return new RedisRateLimitService(rateLimitRedisTemplate, properties);
    }
}