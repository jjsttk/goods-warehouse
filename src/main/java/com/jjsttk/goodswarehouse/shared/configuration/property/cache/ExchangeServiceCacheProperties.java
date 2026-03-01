package com.jjsttk.goodswarehouse.shared.configuration.property.cache;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.cache.exchange-rate-client")
public class ExchangeServiceCacheProperties implements CacheProperties {
    private String cacheName;
    private Duration expireAfterWrite;
}
