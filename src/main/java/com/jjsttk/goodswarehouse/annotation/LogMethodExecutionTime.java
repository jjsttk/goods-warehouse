package com.jjsttk.goodswarehouse.annotation;

import org.springframework.boot.logging.LogLevel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * When applied to a method, an aspect (e.g. with {@code @Around} advice)
 * can intercept the method call, measure the execution duration,
 * and log it using the specified log level.
 * </p>
 *
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * @LogMethodExecutionTime(LogLevel.DEBUG)
 * public void processOrder() {
 *     // business logic
 * }
 * }
 * </pre>
 *
 * <p>By default, the log level is {@link LogLevel#INFO}.</p>
 *
 * @see LogLevel
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LogMethodExecutionTime {

    /**
     * Defines the logging level to be used
     * when writing the method execution time.
     *
     * @return the desired {@link LogLevel}, defaults to {@link LogLevel#INFO}
     */
    LogLevel value();
}
