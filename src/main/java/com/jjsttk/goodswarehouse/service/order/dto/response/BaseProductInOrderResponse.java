package com.jjsttk.goodswarehouse.service.order.dto.response;

import lombok.Builder;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record BaseProductInOrderResponse(
        @NonNull UUID productId,
        @NonNull String name,
        @NonNull BigDecimal quantity,
        @NonNull BigDecimal price
) {
}
