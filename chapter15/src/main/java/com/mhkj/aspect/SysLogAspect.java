package com.mhkj.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mhkj.annotation.SysLog;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 系统日志，切面处理类
 *
 * @author Bruce.Gong
 * @since 1.0.0-SNAPSHOT
 */
@Slf4j
@AllArgsConstructor
@Aspect
@Component
public class SysLogAspect {

    private ObjectMapper objectMapper;

    /**
     * 定义切入点，切入点为 标注了 @SysLog 注解的所有方法
     */
    @Pointcut("@annotation(com.mhkj.annotation.SysLog)")
    public void logPointCut() {
    }

    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long st = System.currentTimeMillis();
        // 执行方法
        Object result = point.proceed();
        // 执行时长(毫秒)
        long time = System.currentTimeMillis() - st;
        // 打印日志
        printSysLog(point, time);
        return result;
    }

    private void printSysLog(ProceedingJoinPoint point, long time) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        try {
            SysLog syslog = method.getAnnotation(SysLog.class);
            String className = point.getTarget().getClass().getName();
            String methodName = signature.getName();
            Object[] args = point.getArgs();
            log.info("操作：{}", syslog.value());
            log.info("请求方法：{}", className + "." + methodName + "()");
            log.info("请求参数：{}", objectMapper.writeValueAsString(args));
            log.info("请求时间：{}", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            log.info    ("请求耗时：{}", time);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}
