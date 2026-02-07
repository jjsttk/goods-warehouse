package com.jjsttk.goodswarehouse.exception.service.customer.inn;

import com.jjsttk.goodswarehouse.exception.service.customer.ExternalServiceException;

public class InnServiceException extends ExternalServiceException {
    private static final String EXCEPTION_MESSAGE_FORMAT =
            "Failed to fetch inn from external service, details: %s";


    public InnServiceException(String details) {
        super(String.format(EXCEPTION_MESSAGE_FORMAT, details));
    }

    public InnServiceException(String details, Throwable cause) {
        super(String.format(EXCEPTION_MESSAGE_FORMAT, details), cause);
    }
}
