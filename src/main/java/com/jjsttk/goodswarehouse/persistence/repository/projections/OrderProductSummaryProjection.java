package com.jjsttk.goodswarehouse.persistence.repository.projections;

import lombok.Builder;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record OrderProductSummaryProjection(
        @NonNull UUID productId,
        @NonNull String name,
        @NonNull BigDecimal quantity,
        @NonNull BigDecimal price
) {
}
