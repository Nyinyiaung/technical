package com.technical.config.ratelimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {
    private final RedisRateLimitService rateLimitService;
    private final ObjectMapper objectMapper;

    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    public Object rateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) 
            RequestContextHolder.currentRequestAttributes()).getRequest();
        String path = request.getRequestURI();
        
        // Check if we should rate limit this path
        if (!rateLimitService.shouldRateLimit(path)) {
            return joinPoint.proceed();
        }

        // Get client IP or other identifier
        String clientId = getClientId(request);
        String rateLimitKey = String.format("%s:%s", path, clientId);

        // Apply rate limiting
        if (rateLimitService.isRateLimited(rateLimitKey, path)) {
            return createRateLimitResponse(path, request);
        }

        return joinPoint.proceed();
    }

    private Object createRateLimitResponse(String path, HttpServletRequest request) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("code", "RATE_LIMIT_EXCEEDED");
        errorResponse.put("message", "Too many requests. Please try again later.");
        errorResponse.put("path", path);

        try {
            HttpServletResponse response = ((ServletRequestAttributes) 
                RequestContextHolder.currentRequestAttributes()).getResponse();
            
            if (response != null) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            }
            return null;
        } catch (Exception e) {
            log.error("Error writing rate limit response", e);
            throw new RuntimeException("Rate limit exceeded", e);
        }
    }

    private String getClientId(HttpServletRequest request) {
        // Get client IP
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}