package com.jjsttk.goodswarehouse.exception.service.order;

import java.util.UUID;

public class NotEnoughQuantityInStockException extends RuntimeException {
    private static final String EXCEPTION_MESSAGE_FORMAT = "Not enough quantity in stock for product productId = %s";

    public NotEnoughQuantityInStockException(UUID id) {
        super(String.format(EXCEPTION_MESSAGE_FORMAT, id));
    }
}
