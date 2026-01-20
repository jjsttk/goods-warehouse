package com.jjsttk.goodswarehouse.mapper.order;

import com.jjsttk.goodswarehouse.persistence.entity.order.OrderEntity;
import com.jjsttk.goodswarehouse.service.order.dto.response.BaseOrderResponse;
import com.jjsttk.goodswarehouse.service.order.product.dto.response.OrderProductProjection;
import com.jjsttk.goodswarehouse.service.order.product.dto.response.OrderProductResponseContainer;

import java.util.UUID;

public interface OrderServiceConverter {
    BaseOrderResponse toResponse(
            UUID orderId,
            OrderProductResponseContainer<OrderProductProjection> serviceResponse
    );

    OrderEntity toEntity(Long customerId, String deliveryAddress);
}
