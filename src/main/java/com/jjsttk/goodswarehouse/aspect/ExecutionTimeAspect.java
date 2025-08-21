package com.jjsttk.goodswarehouse.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Aspect for measuring and logging the execution time of methods annotated with
 * {@link com.jjsttk.goodswarehouse.annotation.LogMethodExecutionTime}.
 *
 * <p>This aspect provides simple performance logging by calculating the time
 * taken to execute a method. It does not measure the transaction duration,
 * only the method execution itself.</p>
 */
@Component
@Aspect
@Slf4j
public final class ExecutionTimeAspect {

    /**
     * Around advice that measures and logs the execution time of a method annotated
     * with {@link com.jjsttk.goodswarehouse.annotation.LogMethodExecutionTime}.
     *
     * @param joinPoint the join point providing information about the method being executed
     * @return the result returned by the method
     * @throws Throwable rethrows any exception thrown by the target method
     */
    @Around("@annotation(com.jjsttk.goodswarehouse.annotation.LogMethodExecutionTime)")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long duration = System.currentTimeMillis() - start;
            log.info(
                    "\u001B[32m" + "Method [{}] executed in {} ms" + "\u001B[0m",
                    joinPoint.getSignature().toShortString(),
                    duration
            );
        }
    }
}
