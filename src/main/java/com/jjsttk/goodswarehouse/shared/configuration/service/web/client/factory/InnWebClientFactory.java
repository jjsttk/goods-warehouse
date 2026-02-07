package com.jjsttk.goodswarehouse.shared.configuration.service.web.client.factory;

import com.jjsttk.goodswarehouse.shared.configuration.property.rest.InnServiceProperties;
import org.springframework.stereotype.Component;

@Component
public final class InnWebClientFactory extends WebClientAbstractFactory<InnServiceProperties> {

    public InnWebClientFactory(InnServiceProperties properties) {
        super(properties);
    }
}
