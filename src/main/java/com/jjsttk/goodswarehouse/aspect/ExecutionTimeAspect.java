package com.jjsttk.goodswarehouse.aspect;

import com.jjsttk.goodswarehouse.annotation.LogMethodExecutionTime;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Component;

/**
 * Aspect for measuring and logging the execution time of methods annotated with
 * {@link LogMethodExecutionTime}.
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
     * with {@link LogMethodExecutionTime}.
     *
     * @param joinPoint the join point providing information about the method being executed
     * @return the result returned by the method
     * @throws Throwable rethrows any exception thrown by the target method
     */
    @Around("@annotation(com.jjsttk.goodswarehouse.annotation.LogMethodExecutionTime)")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        var methodSignature = (MethodSignature) joinPoint.getSignature();
        var method = methodSignature.getMethod();
        var logLevel = method.getAnnotation(LogMethodExecutionTime.class).value();

        long start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long duration = System.currentTimeMillis() - start;

            if (logLevel != LogLevel.OFF) {
                switch (logLevel) {
                    case TRACE -> log.trace("Method [{}] executed in {} ms",
                            joinPoint.getSignature().toShortString(),
                            duration);

                    case DEBUG -> log.debug("Method [{}] executed in {} ms",
                            joinPoint.getSignature().toShortString(),
                            duration);

                    case ERROR -> log.error("Method [{}] executed in {} ms",
                            joinPoint.getSignature().toShortString(),
                            duration);

                    case WARN -> log.warn("Method [{}] executed in {} ms",
                            joinPoint.getSignature().toShortString(),
                            duration);

                    default -> log.info("Method [{}] executed in {} ms",
                            joinPoint.getSignature().toShortString(),
                            duration);
                }
            }
        }
    }
}
