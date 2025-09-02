package com.jjsttk.goodswarehouse.configuration;

import com.jjsttk.goodswarehouse.mapper.ConversionServiceProductConverter;
import com.jjsttk.goodswarehouse.mapper.MapstructProductMapper;
import com.jjsttk.goodswarehouse.mapper.ProductConverter;
import com.jjsttk.goodswarehouse.configuration.properties.MapperProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration class that selects the appropriate {@link ProductConverter}
 * implementation based on application properties.
 */
@Configuration
public class MapperConfig {

    /**
     * Provides the primary {@link ProductConverter} bean.
     * Chooses between MapStruct and ConversionService implementations.
     *
     * @param mapstructMapper        the MapStruct mapper
     * @param conversionServiceMapper the ConversionService mapper
     * @param mapperProperties       properties containing the desired mapper type
     * @return the selected {@link ProductConverter} implementation
     */
    @Bean
    @Primary
    public ProductConverter productConverterSelector(
            MapstructProductMapper mapstructMapper,
            ConversionServiceProductConverter conversionServiceMapper,
            MapperProperties mapperProperties) {

        String type = mapperProperties.getType();

        return switch (type) {
            case "mapstruct" -> mapstructMapper;
            case "conversion-service" -> conversionServiceMapper;
            default -> throw new IllegalArgumentException("Unknown mapper type: " + type);
        };
    }

}
