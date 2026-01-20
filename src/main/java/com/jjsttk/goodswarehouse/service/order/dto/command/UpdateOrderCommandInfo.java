package com.jjsttk.goodswarehouse.service.order.dto.command;

import lombok.Builder;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Builder
public record UpdateOrderCommandInfo(
        @NonNull UUID orderId,
        @NonNull Map<UUID, BigDecimal> productQuantities
) {
}
