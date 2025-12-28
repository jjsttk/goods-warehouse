package com.jjsttk.goodswarehouse.service.product.dto.response;

import com.jjsttk.goodswarehouse.shared.enums.product.Category;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record ProductServiceProductDetailedResponse(
        UUID id,
        String name,
        String article,
        String description,
        Category category,
        BigDecimal price,
        BigDecimal quantity,
        Boolean isAvailable,
        OffsetDateTime lastQuantityModified,
        LocalDate createdAt
) {
}
