package com.jjsttk.goodswarehouse.exception.service.client;

public class WebClientRetryException extends RuntimeException {

    public WebClientRetryException(long maxAttempts, Throwable failure) {
        super(String.format("Retry attempts exhausted after %s retries", maxAttempts), failure);
    }
}
