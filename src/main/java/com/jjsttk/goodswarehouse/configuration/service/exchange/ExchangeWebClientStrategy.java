package com.jjsttk.goodswarehouse.configuration.service.exchange;

import io.netty.handler.timeout.ReadTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

/**
 * Provides retry strategies for WebClient exchange operations.
 * Contains predefined retry filters for common failure scenarios.
 */
@Slf4j
@Component
public final class ExchangeWebClientStrategy {

    /**
     * Creates a retry filter for read timeout errors.
     * Retries only on ReadTimeoutException with fixed delay between attempts.
     *
     * @param maxAttempts maximum number of retry attempts
     * @param backoffDuration delay between retry attempts
     * @return ExchangeFilterFunction that retries on read timeouts
     */
    public ExchangeFilterFunction getReadTimeoutRetryFilterFunction(
            int maxAttempts, Duration backoffDuration
    ) {
        return (request, next) ->
                next.exchange(request)
                        .retryWhen(Retry.fixedDelay(maxAttempts, backoffDuration)
                                .filter(throwable -> throwable instanceof WebClientRequestException
                                        && throwable.getCause() instanceof ReadTimeoutException)

                                .doBeforeRetry(retrySignal ->
                                        log.info("Read timeout retry attempt: {}/{}",
                                                retrySignal.totalRetries() + 1, maxAttempts))

                                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                                    log.warn("All {} read timeout retry attempts exhausted", maxAttempts);
                                    return retrySignal.failure();
                                }));
    }

    /**
     * Creates a retry filter for server errors (5xx status codes).
     * Retries only on InternalServerError (500) status with fixed delay.
     *
     * @param maxAttempts maximum number of retry attempts
     * @param backoffDuration delay between retry attempts
     * @return ExchangeFilterFunction that retries on server errors
     */
    public ExchangeFilterFunction getServerErrorRetryFilterFunction(int maxAttempts, Duration backoffDuration) {
        return (request, next) ->
                next.exchange(request).flatMap(
                                response -> {
                                    if (response.statusCode().is5xxServerError()) {
                                        log.info("Server 5xx error {} detected",
                                                response.statusCode());

                                        return response.createException().flatMap(Mono::error);
                                    }
                                    return Mono.just(response);
                                })

                        .retryWhen(Retry.fixedDelay(maxAttempts, backoffDuration)
                                .filter(throwable ->
                                        throwable instanceof WebClientResponseException.InternalServerError)
                                .doBeforeRetry(retrySignal ->
                                        log.info("Server internal error retry attempt: {}/{}",
                                                retrySignal.totalRetries() + 1, maxAttempts))
                                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                                    log.warn("All {} server error retry attempts exhausted", maxAttempts);
                                    return retrySignal.failure();
                                })
                        );
    }

}
