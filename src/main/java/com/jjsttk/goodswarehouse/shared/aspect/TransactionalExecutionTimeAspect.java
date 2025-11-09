package com.jjsttk.goodswarehouse.shared.aspect;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.concurrent.TimeUnit;


/**
 * Aspect for measuring the total execution time
 * of methods annotated with {@link org.springframework.transaction.annotation.Transactional}.
 *
 * <p>This aspect logs the execution time of a method along with the full transactional scope,
 * including transaction creation, commit, or rollback, which is important for accurately
 * analyzing the performance of transactional operations.</p>
 */
@Slf4j
@ConditionalOnProperty(name = "app.aspect.transactional.measure-execution-time", havingValue = "true")
@Aspect
@Component
public final class TransactionalExecutionTimeAspect {

    /**
     * Around advice that measures the full execution time of a method and its transaction.
     *
     * @param joinPoint the join point providing information about the method being executed
     * @return the result returned by the method
     * @throws Throwable rethrows any exceptions thrown by the target method
     */
    @Around("@annotation(org.springframework.transaction.annotation.Transactional)")
    public Object registerTransactionSynchronization(ProceedingJoinPoint joinPoint) throws Throwable {
        var methodName = joinPoint.getSignature().toShortString();
        log.debug("Starting execution time measurement for method [{}]", methodName);

        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            return joinPoint.proceed();
        } finally {
            var nonTxPartExecTime = stopWatch.getTime(TimeUnit.MILLISECONDS);

            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCompletion(int status) {
                            stopWatch.stop();
                            var totalExecTime = stopWatch.getTime(TimeUnit.MILLISECONDS);
                            var txPartExecTime = totalExecTime - nonTxPartExecTime;
                            log.info("Time measurement result for method [{}]."
                                            + " Non transactional part executed in [{}ms]."
                                            + " Transactional part executed in [{}ms] with status=[{}]."
                                            + " Total execution time is [{}ms].",
                                    methodName,
                                    nonTxPartExecTime,
                                    txPartExecTime,
                                    status == STATUS_COMMITTED ? "COMMITTED" : "ROLLED_BACK",
                                    totalExecTime
                            );
                        }
                    }
            );
        }
    }
}
