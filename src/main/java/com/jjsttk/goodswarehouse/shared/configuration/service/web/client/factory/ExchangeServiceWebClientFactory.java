package com.jjsttk.goodswarehouse.shared.configuration.service.web.client.factory;

import com.jjsttk.goodswarehouse.shared.configuration.property.rest.ExchangeServiceProperties;
import org.springframework.stereotype.Component;

@Component
public final class ExchangeServiceWebClientFactory extends WebClientAbstractFactory<ExchangeServiceProperties> {

    public ExchangeServiceWebClientFactory(ExchangeServiceProperties properties) {
        super(properties);
    }
}
