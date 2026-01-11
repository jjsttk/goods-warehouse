package com.jjsttk.goodswarehouse.service.exchange.provider;

import com.jjsttk.goodswarehouse.shared.enums.exchange.PriceCurrency;
import com.jjsttk.goodswarehouse.service.exchange.dto.response.ExchangeData;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Random;

@Component
@Order(1)
@ConditionalOnProperty(
        name = "app.exchange-rate-client.implementation",
        havingValue = "mock"
)
public final class WebClientExchangeProviderMock implements ExchangeDataProvider {
    private static final String PROVIDER_NAME = "MOCK_BASED";

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

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }
}
