package com.jjsttk.goodswarehouse.shared.configuration.property.service.account;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.rest.account-service")
public class AccountServiceProperties {
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
        private String getAccNumbers;
    }
}
