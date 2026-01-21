package com.jjsttk.goodswarehouse.shared.configuration.property.rest;

import com.jjsttk.goodswarehouse.shared.configuration.property.service.account.AccountServiceProperties;
import com.jjsttk.goodswarehouse.shared.configuration.property.service.exchange.ExchangeServiceProperties;
import com.jjsttk.goodswarehouse.shared.configuration.property.service.tax.TaxPayerServiceProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.rest")
public class RestProperties {
    private ExchangeServiceProperties exchangeService;
    private AccountServiceProperties accountService;
    private TaxPayerServiceProperties taxPayerService;
}
