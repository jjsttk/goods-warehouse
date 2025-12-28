package com.jjsttk.goodswarehouse.service.order.product.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record OrderProductServiceProductSummary(
        UUID productId,
        String name,
        BigDecimal quantity,
        BigDecimal price
) {
}
