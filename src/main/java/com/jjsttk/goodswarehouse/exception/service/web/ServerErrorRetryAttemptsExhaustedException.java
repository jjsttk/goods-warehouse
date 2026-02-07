package com.jjsttk.goodswarehouse.exception.service.web;

public class ServerErrorRetryAttemptsExhaustedException extends RetryExhaustedException {
    private static final String EXCEPTION_MESSAGE_FORMAT = "All %d server error retry attempts exhausted";

    public ServerErrorRetryAttemptsExhaustedException(int maxAttempts) {
        super(String.format(EXCEPTION_MESSAGE_FORMAT, maxAttempts));
    }

    public ServerErrorRetryAttemptsExhaustedException(int maxAttempts, Throwable cause) {
        super(String.format(EXCEPTION_MESSAGE_FORMAT, maxAttempts), cause);
    }
}
