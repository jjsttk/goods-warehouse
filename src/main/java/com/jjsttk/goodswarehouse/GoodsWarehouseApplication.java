package com.jjsttk.goodswarehouse;

import com.jjsttk.goodswarehouse.configuration.property.ExchangeServiceProperties;
import com.jjsttk.goodswarehouse.configuration.property.SchedulingProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCaching
@EnableJpaAuditing
@EnableScheduling
@EnableConfigurationProperties({SchedulingProperties.class, ExchangeServiceProperties.class})
@SpringBootApplication
public class GoodsWarehouseApplication {

    public static void main(String[] args) {
        SpringApplication.run(GoodsWarehouseApplication.class, args);
    }

}
