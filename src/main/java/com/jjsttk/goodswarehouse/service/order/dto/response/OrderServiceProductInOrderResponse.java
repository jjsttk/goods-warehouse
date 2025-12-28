package com.jjsttk.goodswarehouse.service.order.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record OrderServiceProductInOrderResponse(
        UUID productId,
        String name,
        BigDecimal quantity,
        BigDecimal price
) {
}
