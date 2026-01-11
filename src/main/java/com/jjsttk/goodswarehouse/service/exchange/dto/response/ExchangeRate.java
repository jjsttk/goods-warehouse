package com.jjsttk.goodswarehouse.service.exchange.dto.response;

import com.jjsttk.goodswarehouse.shared.enums.exchange.PriceCurrency;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ExchangeRate(
        PriceCurrency currency,
        BigDecimal rate
) {
}
