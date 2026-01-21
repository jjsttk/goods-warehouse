package com.jjsttk.goodswarehouse.shared.configuration.property.service.tax;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.rest.tax-payer-service")
public class TaxPayerServiceProperties {
    private String host;
    private Methods methods = new Methods();

    @Getter
    @Setter
    public static class Methods {
        private PostHttpMethodConfig post = new PostHttpMethodConfig();
    }

    @Getter
    @Setter
    public static class PostHttpMethodConfig {
        private String getTins;
    }
}
