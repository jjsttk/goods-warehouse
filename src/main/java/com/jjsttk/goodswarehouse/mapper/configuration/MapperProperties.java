package com.jjsttk.goodswarehouse.mapper.configuration;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.mapper")
@Getter
@Setter
public class MapperProperties {
    private String type;
}
