package com.technical.config.aop;

import com.technical.commonutil.CommonUtil;
import com.technical.commonutil.MasterCodeBase;
import com.technical.commonutil.SecurityUtil;
import com.technical.commonutil.UserUtil;
import com.technical.config.ratelimit.RedisRateLimitService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class CommonAspect extends MasterCodeBase {

    private final RedisRateLimitService rateLimitService;

    // Pointcut to match all methods in com.example.service package and its subpackages
    @Pointcut("execution(* com.technical.service..*(..)) || execution(* com.technical.controller..*(..))")
    public void serviceMethods() {}

    // Log aspect
    @Around("serviceMethods()")
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        // Process arguments to mask sensitive fields
        String argsString = Arrays.stream(joinPoint.getArgs())
                .map(SecurityUtil::maskPassword)
                .collect(Collectors.joining(", ", "[", "]"));

        Instant start = Instant.now();
        log.info("[START] {}.{}() with arguments = {}", className, methodName, argsString);

        Object result = null;
        try {
            result = joinPoint.proceed();
            return result;
        } finally {
            Instant finish = Instant.now();
            long duration = Duration.between(start, finish).toMillis();
            if (result != null) {
                log.info("[END] {}.{}() with result = {} ({} ms)", className, methodName, result, duration);
            } else {
                log.info("[END] {}.{}() ({} ms)", className, methodName, duration);
            }
        }
    }

    // Rate limit aspect
    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    public Object rateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes()).getRequest();

        if (rateLimitService.isRateLimited(request)) {
            return createErrorResponse("Too many requests. Please try again later.", request.getRequestURI(), "RATE_LIMIT_EXCEEDED", HttpStatus.TOO_MANY_REQUESTS);
        }

        return joinPoint.proceed();
    }
}
