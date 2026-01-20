package com.jjsttk.goodswarehouse.service.exchange.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jjsttk.goodswarehouse.service.exchange.dto.response.deserializer.ExchangeDataDeserializer;
import com.jjsttk.goodswarehouse.shared.enums.exchange.PriceCurrency;
import lombok.Builder;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.util.Map;

@Builder
@JsonDeserialize(using = ExchangeDataDeserializer.class)
public record ExchangeData(
        @NonNull Map<PriceCurrency, BigDecimal> rates
) {
}
