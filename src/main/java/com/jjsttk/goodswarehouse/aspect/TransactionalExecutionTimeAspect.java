package com.jjsttk.goodswarehouse.aspect;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;


/**
 *  Aspect for measuring the total execution time
 *  of methods annotated with {@link org.springframework.transaction.annotation.Transactional}.
 *
 * <p>This aspect logs the execution time of a method along with the full transactional scope,
 * including transaction creation, commit, or rollback, which is important for accurately
 * analyzing the performance of transactional operations.</p>
 *
 *
 */
@Slf4j
@ConditionalOnProperty(name = "app.aspect.transactional.measure-execution-time", havingValue = "true")
@Aspect
@Component
public final class TransactionalExecutionTimeAspect {
    @Value(value = "${app.aspect.transactional.measure-execution-time}")
    private boolean enabled;

    private static final ThreadLocal<Long> BASE_METHOD_TIME = new ThreadLocal<>();
    private static final ThreadLocal<Long> TRANSACTIONAL_TIME = new ThreadLocal<>();
    private static final ThreadLocal<Long> BASE_METHOD_END_TIME = new ThreadLocal<>();

    @PostConstruct
    public void init() {
        log.info(
                "\u001B[32m" + "Measure method execution time for @Transactional is enabled" + "\u001B[0m"
        );
    }

    /**
     * Around advice that measures the full execution time of a method and its transaction.
     *
     * @param joinPoint the join point providing information about the method being executed
     * @return the result returned by the method
     * @throws Throwable rethrows any exceptions thrown by the target method
     */
    @Around("@annotation(org.springframework.transaction.annotation.Transactional)")
    public Object measureTransactionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        var methodName = joinPoint.getSignature().toShortString();
        var methodStartTime = System.currentTimeMillis();

        log.info(
                "\u001B[32m" + "Starting execution time measurement for method [{}]" + "\u001B[0m",
                methodName
        );

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCompletion(int status) {
                            var txDuration = System.currentTimeMillis() - BASE_METHOD_END_TIME.get();
                            TRANSACTIONAL_TIME.set(txDuration);

                            log.info(
                                    "\u001B[32m" + "Transactional part of method [{}] executed in {}ms, status={}"
                                            + "\u001B[0m",
                                    methodName,
                                    txDuration,
                                    status == STATUS_COMMITTED ? "COMMITTED" : "ROLLED_BACK"
                            );

                            log.info(
                                    "\u001B[32m" + "Total execution time of method [{}] is [{}ms]"
                                    + "\u001B[0m",
                                    methodName,
                                    BASE_METHOD_TIME.get() + TRANSACTIONAL_TIME.get()
                            );
                        }
                    });
        }
        try {
            return joinPoint.proceed();
        } finally {
            BASE_METHOD_END_TIME.set(System.currentTimeMillis());
            BASE_METHOD_TIME.set(BASE_METHOD_END_TIME.get() - methodStartTime);

            log.info(
                    "\u001B[32m" + "Non transactional part of method [{}] executed in {}ms" + "\u001B[0m",
                    methodName,
                    BASE_METHOD_TIME.get()
            );
        }
    }
}
