package com.jjsttk.goodswarehouse.service.product.dto.response;

import lombok.Builder;

import java.util.Map;
import java.util.UUID;

@Builder
public record ProductServiceReservationResponse(
        Map<UUID, ProductServiceReservedProductInfo> productInfo
) {
}
