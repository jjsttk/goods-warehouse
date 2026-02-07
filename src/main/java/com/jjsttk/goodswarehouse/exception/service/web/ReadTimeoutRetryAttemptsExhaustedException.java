package com.jjsttk.goodswarehouse.exception.service.web;

public class ReadTimeoutRetryAttemptsExhaustedException extends RetryExhaustedException {
    private static final String EXCEPTION_MESSAGE_FORMAT = "All %d read timeout retry attempts exhausted";

    public ReadTimeoutRetryAttemptsExhaustedException(int maxAttempts) {
        super(String.format(EXCEPTION_MESSAGE_FORMAT, maxAttempts));
    }

    public ReadTimeoutRetryAttemptsExhaustedException(int maxAttempts, Throwable cause) {
        super(String.format(EXCEPTION_MESSAGE_FORMAT, maxAttempts), cause);
    }
}
