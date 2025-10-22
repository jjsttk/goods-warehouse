package com.jjsttk.goodswarehouse.service.exchange.client;

import com.jjsttk.goodswarehouse.enums.PriceCurrency;
import com.jjsttk.goodswarehouse.service.exchange.request.ExchangeData;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Random;

@Component
@ConditionalOnProperty(
        name = "app.exchange-rate-client.implementation",
        havingValue = "mock"
)
public final class ExchangeRateClientImplMock implements ExchangeRateClient {

    @Override
    public ExchangeData getExchangeData() {
        var map = new HashMap<PriceCurrency, BigDecimal>();
        var random = new Random();
        map.put(PriceCurrency.USD, BigDecimal.valueOf(random.nextDouble()));
        map.put(PriceCurrency.EUR, BigDecimal.valueOf(random.nextDouble()));
        map.put(PriceCurrency.CNY, BigDecimal.valueOf(random.nextDouble()));

        return ExchangeData.builder()
                .rates(map)
                .build();
    }
}
