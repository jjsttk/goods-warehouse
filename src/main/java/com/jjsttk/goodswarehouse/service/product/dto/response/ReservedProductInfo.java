package com.jjsttk.goodswarehouse.service.product.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ReservedProductInfo(
        BigDecimal reservedQuantity,
        BigDecimal priceAtMoment
) {
}
