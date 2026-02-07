package com.jjsttk.goodswarehouse.shared.configuration.property.rest;

import com.jjsttk.goodswarehouse.shared.configuration.property.common.webclient.RetrySettings;
import com.jjsttk.goodswarehouse.shared.configuration.property.common.webclient.TimeoutSettings;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.rest.exchange-service")
public class ExchangeServiceProperties implements RestServiceProperties {
    private String host;
    private String fallbackFile;
    private Endpoints endpoints = new Endpoints();

    @NestedConfigurationProperty
    private TimeoutSettings timeout = new TimeoutSettings();

    @NestedConfigurationProperty
    private RetrySettings retry = new RetrySettings();

    @Getter
    @Setter
    public static class Endpoints {
        private String currencies;
    }
}
