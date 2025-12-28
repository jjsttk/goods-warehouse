package com.jjsttk.goodswarehouse.service.product.price.exchange.dto.response;

import com.jjsttk.goodswarehouse.service.product.dto.response.ProductServiceProductDetailedResponse;
import com.jjsttk.goodswarehouse.shared.enums.product.Category;
import com.jjsttk.goodswarehouse.shared.enums.exchange.PriceCurrency;
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
    /**
     * Creates a new response from ProductServiceProductDetailedResponse with converted price and currency.
     */
    public static ProductPriceExchangeServiceResponse from(
            ProductServiceProductDetailedResponse baseResponse,
            BigDecimal convertedPrice,
            PriceCurrency currency
    ) {
        return new ProductPriceExchangeServiceResponse(
                baseResponse.id(),
                baseResponse.name(),
                baseResponse.article(),
                baseResponse.description(),
                baseResponse.category(),
                convertedPrice,
                baseResponse.quantity(),
                currency,
                baseResponse.isAvailable(),
                baseResponse.lastQuantityModified(),
                baseResponse.createdAt()
        );
    }
}
