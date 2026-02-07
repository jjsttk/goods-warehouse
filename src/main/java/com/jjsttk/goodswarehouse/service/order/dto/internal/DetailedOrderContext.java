package com.jjsttk.goodswarehouse.service.order.dto.internal;

import com.jjsttk.goodswarehouse.persistence.repository.projections.OrderDetailedProjection;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record DetailedOrderContext(
        Map<Long, String> customerIdToLoginMap,
        Map<UUID, List<OrderDetailedProjection>> groupedByProductIdProjectionMap
) {
}
