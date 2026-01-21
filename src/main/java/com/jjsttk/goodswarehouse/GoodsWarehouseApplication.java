package com.jjsttk.goodswarehouse;

import com.jjsttk.goodswarehouse.shared.configuration.property.cache.CacheProperties;
import com.jjsttk.goodswarehouse.shared.configuration.property.rest.RestProperties;
import com.jjsttk.goodswarehouse.shared.configuration.property.service.account.AccountServiceProperties;
import com.jjsttk.goodswarehouse.shared.configuration.property.service.exchange.ExchangeServiceProperties;
import com.jjsttk.goodswarehouse.shared.configuration.property.service.scheduling.SchedulingProperties;
import com.jjsttk.goodswarehouse.shared.configuration.property.service.tax.TaxPayerServiceProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCaching
@EnableJpaAuditing
@EnableScheduling
@EnableConfigurationProperties({
        SchedulingProperties.class, RestProperties.class,
        ExchangeServiceProperties.class, CacheProperties.class,
        TaxPayerServiceProperties.class, AccountServiceProperties.class
})
@SpringBootApplication
public class GoodsWarehouseApplication {

    public static void main(String[] args) {
        SpringApplication.run(GoodsWarehouseApplication.class, args);
    }

}
