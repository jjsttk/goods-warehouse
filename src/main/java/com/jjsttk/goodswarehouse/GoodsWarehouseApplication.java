package com.jjsttk.goodswarehouse;

import com.jjsttk.goodswarehouse.configuration.property.SchedulingProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@EnableScheduling
@EnableConfigurationProperties(SchedulingProperties.class)
@SpringBootApplication
public class GoodsWarehouseApplication {

    public static void main(String[] args) {
        SpringApplication.run(GoodsWarehouseApplication.class, args);
    }

}
