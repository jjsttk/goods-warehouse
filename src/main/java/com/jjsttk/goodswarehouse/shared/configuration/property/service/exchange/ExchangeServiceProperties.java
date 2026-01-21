package com.jjsttk.goodswarehouse.shared.configuration.property.service.exchange;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.rest.exchange-service")
public class ExchangeServiceProperties {
    private String host;
    private String fallbackFile;
    private Methods methods = new Methods();
    private TimeoutSettings timeout = new TimeoutSettings();

    @Getter
    @Setter
    public static class Methods {
        private GetHttpMethodConfig get = new GetHttpMethodConfig();
    }

    @Getter
    @Setter
    public static class GetHttpMethodConfig {
        private String currencies;
    }

    @Getter
    @Setter
    public static class TimeoutSettings {
        private Duration connect = Duration.ofSeconds(5);
        private Duration read = Duration.ofSeconds(3);
        private RetrySettings retry = new RetrySettings();
    }

    @Getter
    @Setter
    public static class RetrySettings {
        private RetryPolicy readTimeout = new RetryPolicy();
        private RetryPolicy serverErrors = new RetryPolicy();
    }

    @Getter
    @Setter
    public static class RetryPolicy {
        private int maxAttempts = 3;
        private Duration backoff = Duration.ofSeconds(1);
    }
}
