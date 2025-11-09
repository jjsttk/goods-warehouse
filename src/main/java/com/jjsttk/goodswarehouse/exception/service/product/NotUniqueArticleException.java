package com.jjsttk.goodswarehouse.exception.service.product;

import lombok.Getter;

import java.util.UUID;

@Getter
public class NotUniqueArticleException extends RuntimeException {
    private static final String EXCEPTION_MESSAGE_FORMAT = "Product with id = %s already uses this article";
    private final UUID productId;

    public NotUniqueArticleException(UUID id) {
        super(String.format(EXCEPTION_MESSAGE_FORMAT, id));
        this.productId = id;
    }
}
