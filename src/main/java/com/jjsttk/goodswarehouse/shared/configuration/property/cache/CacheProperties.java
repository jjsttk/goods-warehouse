package com.jjsttk.goodswarehouse.shared.configuration.property.cache;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.cache")
public class CacheProperties {
    private ExchangeRateClientProperties exchangeRateClient = new ExchangeRateClientProperties();

    @Getter
    @Setter
    public static class ExchangeRateClientProperties {
        private String cacheName = "exchangeRateClientCache";
        private boolean recordStats = false;
        private Duration expireAfterWrite = Duration.ofMinutes(5);

    }
}
