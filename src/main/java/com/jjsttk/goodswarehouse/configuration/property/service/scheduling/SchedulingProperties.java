package com.jjsttk.goodswarehouse.configuration.property.service.scheduling;

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
    private Optimization optimization = new Optimization();

    @Getter
    @Setter
    public static class Optimization {
        private boolean enabled = false;
        private boolean useExclusiveLock = false;
        private String outputFileName = "scheduling-result.log";
    }
}
