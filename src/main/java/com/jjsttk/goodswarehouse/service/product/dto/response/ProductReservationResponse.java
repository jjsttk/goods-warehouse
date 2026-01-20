package com.jjsttk.goodswarehouse.service.product.dto.response;

import com.jjsttk.goodswarehouse.shared.enums.product.ReservationStatus;
import lombok.Builder;

import java.util.Map;
import java.util.UUID;

@Builder
public record ProductReservationResponse(
        Map<UUID, ReservedProductInfo> reservedProductsInfoMap,
        Map<UUID, ReservationStatus> problemsMap
) {
    public boolean hasProblems() {
        return !problemsMap.isEmpty();
    }
}
