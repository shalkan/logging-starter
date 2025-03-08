package ru.shalkan.loggingstarter.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;

import java.lang.reflect.Method;

@Aspect
public class LogExecutionAspect {

    private static final Logger logger = LoggerFactory.getLogger(LogExecutionAspect.class);

    @Around("@annotation(ru.shalkan.loggingstarter.annotation.LogExecutionTime)")
    public Object aroundLogExecutionTimeMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        try {
            return joinPoint.proceed();
        } catch (BeansException e) {
            throw e.getCause();
        } finally {
            logger.info("Время выполнения метода {}: {}", method.getName(), System.currentTimeMillis() - startTime);
        }
    }
}
