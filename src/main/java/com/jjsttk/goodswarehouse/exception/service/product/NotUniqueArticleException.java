package com.jjsttk.goodswarehouse.exception.service.product;

import lombok.Getter;

import java.util.UUID;

@Getter
public class NotUniqueArticleException extends RuntimeException {
    private final UUID productId;

    public NotUniqueArticleException(UUID id) {
        super(String.format("Product with id %s already uses this article", id));
        this.productId = id;
    }
}
