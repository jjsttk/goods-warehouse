package com.jjsttk.goodswarehouse.exception.service.order.product;

import java.util.Collection;

public class ProductsToOrderNotFoundException extends RuntimeException {
    private static final String EXCEPTION_MESSAGE_FORMAT = "Products not found with id`s: %s";

    public ProductsToOrderNotFoundException(Collection<?> productIds) {
        super(String.format(EXCEPTION_MESSAGE_FORMAT, productIds.toString()));

    }
}
