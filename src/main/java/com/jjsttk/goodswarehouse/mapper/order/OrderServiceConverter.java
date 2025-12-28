package com.jjsttk.goodswarehouse.mapper.order;

import com.jjsttk.goodswarehouse.persistence.entity.order.OrderEntity;
import com.jjsttk.goodswarehouse.service.order.dto.response.BaseOrderServiceResponse;
import com.jjsttk.goodswarehouse.service.order.product.dto.response.OrderProductServiceProductSummary;
import com.jjsttk.goodswarehouse.service.order.product.dto.response.OrderProductServiceResponseContainer;

import java.util.UUID;

public interface OrderServiceConverter {
    BaseOrderServiceResponse toResponse(
            UUID orderId,
            OrderProductServiceResponseContainer<OrderProductServiceProductSummary> serviceResponse
    );

    OrderEntity toEntity(Long customerId, String deliveryAddress);
}
