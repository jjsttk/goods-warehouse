package com.jjsttk.goodswarehouse.service.product.price.exchange.dto.response;

import com.jjsttk.goodswarehouse.shared.enums.exchange.PriceCurrency;
import com.jjsttk.goodswarehouse.shared.enums.product.Category;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record ProductPriceExchangeServiceResponse(
        UUID id,
        String name,
        String article,
        String description,
        Category category,
        BigDecimal price,
        BigDecimal quantity,
        PriceCurrency currency,
        Boolean isAvailable,
        OffsetDateTime lastQuantityModified,
        LocalDate createdAt
) {
}
