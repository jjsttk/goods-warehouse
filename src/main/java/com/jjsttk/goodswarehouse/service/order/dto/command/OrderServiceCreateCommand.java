package com.jjsttk.goodswarehouse.service.order.dto.command;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Builder
public record OrderServiceCreateCommand(
        String deliveryAddress,
        Map<UUID, BigDecimal> productQuantities
) {
}
