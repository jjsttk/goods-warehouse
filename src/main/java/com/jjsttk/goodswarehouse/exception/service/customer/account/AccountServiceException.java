package com.jjsttk.goodswarehouse.exception.service.customer.account;

import com.jjsttk.goodswarehouse.exception.service.customer.ExternalServiceException;

public class AccountServiceException extends ExternalServiceException {
    private static final String EXCEPTION_MESSAGE_FORMAT =
            "Failed to fetch account number from external service, details: %s";

    public AccountServiceException(String details) {
        super(String.format(EXCEPTION_MESSAGE_FORMAT, details));
    }

    public AccountServiceException(String details, Throwable cause) {
        super(String.format(EXCEPTION_MESSAGE_FORMAT, details), cause);
    }
}
