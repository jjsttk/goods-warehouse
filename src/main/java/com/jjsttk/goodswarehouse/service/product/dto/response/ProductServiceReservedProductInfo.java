package com.jjsttk.goodswarehouse.service.product.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductServiceReservedProductInfo(
        BigDecimal reservedQuantity,
        BigDecimal priceAtMoment
) {
}
