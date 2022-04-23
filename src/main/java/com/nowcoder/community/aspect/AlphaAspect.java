package com.nowcoder.community.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author : zhiHao
 * @since : 2022/4/23
 */
@Component
@Aspect
public class AlphaAspect {

    private static final Logger logger = LoggerFactory.getLogger(AlphaAspect.class);

    @Pointcut("execution(* com.nowcoder.community.service.*.*(..))")
    public void pointCut() {
    }

    @Before("pointCut()")
    public void before() {
        logger.debug("before");
    }

    @After("pointCut()")
    public void after() {
        logger.debug("after");
    }

    @AfterReturning("pointCut()")
    public void afterReturning() {
        logger.debug("afterReturning");
    }

    @AfterThrowing("pointCut()")
    public void afterThrowing() {
        logger.debug("afterThrowing");
    }

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.debug("around before");
        Object object = joinPoint.proceed();
        logger.debug("around after");
        return object;
    }
}
