package com.jjsttk.goodswarehouse.mapper.configuration;

import com.jjsttk.goodswarehouse.mapper.ConversionServiceProductConverter;
import com.jjsttk.goodswarehouse.mapper.MapstructProductMapper;
import com.jjsttk.goodswarehouse.mapper.ProductConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Slf4j
@Configuration
public class MapperSelector {

    @Bean
    @Primary
    public ProductConverter productConverterSelector(
            MapstructProductMapper mapstructMapper,
            ConversionServiceProductConverter conversionServiceMapper,
            MapperProperties mapperProperties) {

        String type = mapperProperties.getType();
        log.info("Using mapper type: {}", type);
        return switch (type) {
            case "mapstruct" -> mapstructMapper;
            case "conversion-service" -> conversionServiceMapper;
            default -> throw new IllegalArgumentException("Unknown mapper type: " + type);
        };
    }

}
