package com.jjsttk.goodswarehouse.mapper.order.product;

import com.jjsttk.goodswarehouse.persistence.entity.order.product.OrderProductEntity;
import com.jjsttk.goodswarehouse.service.order.product.dto.response.OrderProductResponseContainer;
import com.jjsttk.goodswarehouse.service.product.dto.response.ReservedProductInfo;

import java.util.Collection;
import java.util.UUID;

public interface OrderProductConverter {
    OrderProductEntity toEntity(
            UUID productId,
            ReservedProductInfo productInfo
    );

    void update(
            OrderProductEntity orderProductEntity,
            ReservedProductInfo updateInfo
    );

    default <T> OrderProductResponseContainer<T> toResponse(Collection<T> productSummariesByOrderId) {
        return new OrderProductResponseContainer<>(productSummariesByOrderId);
    }
}
