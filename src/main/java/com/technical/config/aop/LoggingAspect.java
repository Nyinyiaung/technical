package com.technical.config.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    // Pointcut to match all methods in com.example.service package and its subpackages
    @Pointcut("execution(* com.technical.service..*(..)) || execution(* com.technical.controller..*(..))")
    public void serviceMethods() {}

    @Around("serviceMethods()")
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        Instant start = Instant.now();
        log.info("[START] {}.{}() with arguments = {}", className, methodName, Arrays.toString(args));

        Object result = joinPoint.proceed(); // call the method

        Instant finish = Instant.now();
        long duration = Duration.between(start, finish).toMillis();
        log.info("[END] {}.{}() with result = {} ({} ms)", className, methodName, result, duration);
        return result;
    }
}
