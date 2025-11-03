package com.jjsttk.goodswarehouse.configuration.property.rest;

import com.jjsttk.goodswarehouse.configuration.property.service.exchange.ExchangeServiceProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.rest")
public class RestProperties {
    private ExchangeServiceProperties exchangeService;
}
