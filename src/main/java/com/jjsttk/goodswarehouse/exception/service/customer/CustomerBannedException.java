package com.jjsttk.goodswarehouse.exception.service.customer;

public class CustomerBannedException extends RuntimeException {
    private static final String EXCEPTION_MESSAGE_FORMAT = "Customer with id = %s was banned.";

    public CustomerBannedException(Long customerId) {
        super(String.format(EXCEPTION_MESSAGE_FORMAT, customerId));
    }
}
