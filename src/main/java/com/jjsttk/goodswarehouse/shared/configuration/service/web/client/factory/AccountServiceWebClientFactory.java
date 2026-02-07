package com.jjsttk.goodswarehouse.shared.configuration.service.web.client.factory;

import com.jjsttk.goodswarehouse.shared.configuration.property.rest.AccountServiceProperties;
import org.springframework.stereotype.Component;

@Component
public final class AccountServiceWebClientFactory extends WebClientAbstractFactory<AccountServiceProperties> {

    public AccountServiceWebClientFactory(AccountServiceProperties properties) {
        super(properties);
    }
}
