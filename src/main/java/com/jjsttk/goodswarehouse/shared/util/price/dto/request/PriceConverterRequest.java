package com.jjsttk.goodswarehouse.shared.util.price.dto.request;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PriceConverterRequest(
        BigDecimal sourcePrice,
        BigDecimal actualRate
) {
}
