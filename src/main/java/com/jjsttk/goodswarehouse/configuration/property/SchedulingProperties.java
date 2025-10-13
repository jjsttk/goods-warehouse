package com.jjsttk.goodswarehouse.configuration.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.scheduling")
public class SchedulingProperties {

    private boolean enabled;
    private long period;
    private BigDecimal priceIncreasePercentage;
    private Optimization optimization;

    @Getter
    @Setter
    public static class Optimization {
        private boolean enabled;
        private boolean useExclusiveLock;
        private String outputFileName = "scheduling-result.log";
    }
}
