package com.jjsttk.goodswarehouse.service.order.dto.response;

import lombok.Builder;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record BaseOrderResponse(
        @NonNull UUID orderId,
        @NonNull List<BaseProductInOrderResponse> products,
        @NonNull BigDecimal totalPrice
) {
}
