package com.jjsttk.goodswarehouse.exception.service.order;

public class NotYourOrderException extends RuntimeException {
    private static final String EXCEPTION_MESSAGE_FORMAT =
            "The customer with id = %s is attempting to interact with another customer's order";

    public NotYourOrderException(Long customerId) {
        super(String.format(EXCEPTION_MESSAGE_FORMAT, customerId));
    }
}
