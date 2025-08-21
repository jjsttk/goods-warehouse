package com.jjsttk.goodswarehouse.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ProductResponseDto(
        UUID id,
        String name,
        Long article,
        String description,
        String category,
        BigDecimal price,
        Integer quantity,
        LocalDateTime lastQuantityModified,
        LocalDateTime createdAt
) {
}
