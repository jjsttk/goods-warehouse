package com.jjsttk.goodswarehouse.service.exchange.response;

import com.jjsttk.goodswarehouse.enums.PriceCurrency;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ExchangeServiceResponse(
        BigDecimal price,
        PriceCurrency currency
) {
}
