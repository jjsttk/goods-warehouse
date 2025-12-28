package com.jjsttk.goodswarehouse.exception.service.order;

import com.jjsttk.goodswarehouse.shared.enums.order.OrderStatus;

import java.util.UUID;

public class OrderCannotBeCancelledException extends RuntimeException {
    private static final String EXCEPTION_MESSAGE_FORMAT = "Order with id: %s cannot be cancelled, status is: %s";

    public OrderCannotBeCancelledException(UUID orderId, OrderStatus status) {
        super(String.format(EXCEPTION_MESSAGE_FORMAT, orderId, status));
    }
}
