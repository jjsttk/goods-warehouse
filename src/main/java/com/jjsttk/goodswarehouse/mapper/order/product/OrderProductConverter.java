package com.jjsttk.goodswarehouse.mapper.order.product;

import com.jjsttk.goodswarehouse.persistence.entity.order.product.OrderProductEntity;
import com.jjsttk.goodswarehouse.service.order.product.dto.response.OrderProductServiceResponseContainer;
import com.jjsttk.goodswarehouse.service.product.dto.response.ProductServiceReservedProductInfo;

import java.util.List;
import java.util.UUID;

public interface OrderProductConverter {
    OrderProductEntity toEntity(
            UUID productId,
            ProductServiceReservedProductInfo productInfo
    );

    void update(
            OrderProductEntity orderProductEntity,
            ProductServiceReservedProductInfo updateInfo
    );

    default <T> OrderProductServiceResponseContainer<T> toResponse(List<T> productSummariesByOrderId) {
        return new OrderProductServiceResponseContainer<>(productSummariesByOrderId);
    }
}
