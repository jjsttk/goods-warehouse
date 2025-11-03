package com.jjsttk.goodswarehouse.service.exchange.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jjsttk.goodswarehouse.enums.PriceCurrency;
import com.jjsttk.goodswarehouse.service.exchange.request.deserializer.ExchangeDataDeserializer;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Map;

@Builder
@JsonDeserialize(using = ExchangeDataDeserializer.class)
public record ExchangeData(
        Map<PriceCurrency, BigDecimal> rates
) {
}
