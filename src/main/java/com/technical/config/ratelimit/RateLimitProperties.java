package com.technical.config.ratelimit;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.AntPathMatcher;

import java.util.Map;
import java.util.Optional;

@Data
@ConfigurationProperties(prefix = "rate.limit")
@Slf4j
public class RateLimitProperties {
    private boolean enabled = true;
    private Map<String, EndpointConfig> config;

    @Data
    public static class EndpointConfig {
        private int limit;
        private int window;
    }

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public EndpointConfig getConfigForPath(String path) {
        if (config == null) {
            return null;
        }
        
        // First try exact match
        Optional<Map.Entry<String, EndpointConfig>> exactMatch = config.entrySet().stream()
            .filter(entry -> entry.getKey().equals(path))
            .findFirst();

        if (exactMatch.isPresent()) {
            return exactMatch.get().getValue();
        }
        
        // If no exact match, try pattern matching
        Optional<Map.Entry<String, EndpointConfig>> patternMatch = config.entrySet().stream()
            .filter(entry -> pathMatcher.match(entry.getKey(), path))
            .findFirst();
            
        return patternMatch.map(Map.Entry::getValue).orElse(null);
    }
}