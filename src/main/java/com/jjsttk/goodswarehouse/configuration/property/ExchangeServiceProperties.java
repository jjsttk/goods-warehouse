package com.jjsttk.goodswarehouse.configuration.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.exchange-service")
public class ExchangeServiceProperties {
    private Api api;
    private int retryAttempts;
    private String fallbackFile;

    @Getter
    @Setter
    public static class Api {
        private String host;
        private Endpoint currencies;

        @Getter
        @Setter
        public static class Endpoint {
            private String path;
        }
    }
}
