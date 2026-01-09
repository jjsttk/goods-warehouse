package com.jjsttk.goodswarehouse.service.exchange.util.converter.dto.request;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PriceConverterRequest(
        BigDecimal sourcePrice,
        BigDecimal actualRate
) {
}
