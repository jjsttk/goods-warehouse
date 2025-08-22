package com.jjsttk.goodswarehouse.model.listener;

import com.jjsttk.goodswarehouse.model.entity.Product;
import jakarta.persistence.PrePersist;

import java.time.LocalDateTime;

public final class ProductQuantityUpdateListener {
    @PrePersist
    public void prePersist(Product product) {
        if (product.getQuantity() == null) {
            product.setQuantity(0);
        }
        product.setLastQuantityModified(LocalDateTime.now());
    }
}
