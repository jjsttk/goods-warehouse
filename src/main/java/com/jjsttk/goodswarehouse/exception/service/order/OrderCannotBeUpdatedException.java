package com.jjsttk.goodswarehouse.exception.service.order;

import com.jjsttk.goodswarehouse.shared.enums.order.OrderStatus;

import java.util.UUID;

public class OrderCannotBeUpdatedException extends RuntimeException {
    private static final String EXCEPTION_MESSAGE_FORMAT = "Order with id: %s cannot be updated, status is: %s";

    public OrderCannotBeUpdatedException(UUID orderId, OrderStatus status) {
        super(String.format(EXCEPTION_MESSAGE_FORMAT, orderId, status));
    }
}
