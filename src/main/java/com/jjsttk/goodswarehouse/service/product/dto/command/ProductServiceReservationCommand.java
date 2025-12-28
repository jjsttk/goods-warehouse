package com.jjsttk.goodswarehouse.service.product.dto.command;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Builder
public record ProductServiceReservationCommand(
        Map<UUID, BigDecimal> productQuantities
) {
}
