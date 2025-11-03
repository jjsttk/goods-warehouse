package com.jjsttk.goodswarehouse.service.product.response;

import com.jjsttk.goodswarehouse.enums.Category;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record BaseProductServiceDto(
        UUID id,
        String name,
        String article,
        String description,
        Category category,
        BigDecimal price,
        BigDecimal quantity,
        OffsetDateTime lastQuantityModified,
        LocalDate createdAt
) {
}
