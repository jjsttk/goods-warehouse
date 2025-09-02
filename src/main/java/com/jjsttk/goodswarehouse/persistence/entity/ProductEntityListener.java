package com.jjsttk.goodswarehouse.persistence.entity;

import jakarta.persistence.PrePersist;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public final class ProductEntityListener {
    @PrePersist
    public void prePersist(ProductEntity productEntity) {
        if (productEntity.getQuantity() == null) {
            productEntity.setQuantity(new BigDecimal("0"));
        }

        productEntity.setCreatedAt(OffsetDateTime.now());
        productEntity.setLastQuantityModified(OffsetDateTime.now());
    }
}
