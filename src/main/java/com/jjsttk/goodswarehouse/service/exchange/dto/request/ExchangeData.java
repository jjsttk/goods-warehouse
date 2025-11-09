package com.jjsttk.goodswarehouse.service.exchange.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jjsttk.goodswarehouse.service.exchange.dto.request.deserializer.ExchangeDataDeserializer;
import com.jjsttk.goodswarehouse.shared.enums.PriceCurrency;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Map;

@Builder
@JsonDeserialize(using = ExchangeDataDeserializer.class)
public record ExchangeData(
        Map<PriceCurrency, BigDecimal> rates
) {
}
