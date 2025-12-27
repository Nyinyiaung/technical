package com.technical.config.ratelimit;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.AntPathMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Data
@ConfigurationProperties(prefix = "rate.limit")
@Slf4j
public class RateLimitProperties {
    private boolean enabled = true;
    private List<String> include = new ArrayList<>();
    private List<String> exclude = new ArrayList<>();
    private DefaultLimit df = new DefaultLimit();
    private Map<String, EndpointConfig> config;

    @Data
    public static class DefaultLimit {
        private int limit;
        private int window; // seconds
    }

    @Data
    public static class EndpointConfig {
        private int limit;
        private int window;
    }

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public Optional<EndpointConfig> getConfigForPath(String path) {
        if (config == null) {
            return Optional.empty();
        }
        
        // First try exact match
        Optional<Map.Entry<String, EndpointConfig>> exactMatch = config.entrySet().stream()
            .filter(entry -> entry.getKey().equals(path))
            .findFirst();

        if (exactMatch.isPresent()) {
            return exactMatch.map(Map.Entry::getValue);
        }
        
        // If no exact match, try pattern matching
        return config.entrySet().stream()
            .filter(entry -> pathMatcher.match(entry.getKey(), path))
            .map(Map.Entry::getValue)
            .findFirst();
    }

    public boolean isPathIncluded(String path) {
        // Check if explicitly excluded
        if (exclude != null && exclude.stream().anyMatch(pattern -> 
            pathMatcher.match(pattern, path))) {
            return false;
        }

        // If includes are specified, only rate limit included paths
        if (include != null && !include.isEmpty()) {
            return include.stream().anyMatch(pattern -> 
                pathMatcher.match(pattern, path));
        }

        // Otherwise, rate limit all paths except excluded ones
        return true;
    }
}