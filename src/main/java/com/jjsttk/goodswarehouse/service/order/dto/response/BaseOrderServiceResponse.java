package com.jjsttk.goodswarehouse.service.order.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record BaseOrderServiceResponse(
        UUID orderId,
        List<OrderServiceProductInOrderResponse> products,
        BigDecimal totalPrice
) {
}
