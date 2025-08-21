package com.jjsttk.goodswarehouse.configuration;

import com.jjsttk.goodswarehouse.mapper.ConversionServiceProductConverter;
import com.jjsttk.goodswarehouse.mapper.MapstructProductMapper;
import com.jjsttk.goodswarehouse.mapper.ProductConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration class that selects the appropriate {@link ProductConverter}
 * implementation based on application properties.
 */
@Configuration
public class MapperConfig {

    @Value("${app.mapper.type:conversion-service}")
    private String type;
    /**
     * Provides the primary {@link ProductConverter} bean.
     * Chooses between MapStruct and ConversionService implementations.
     *
     * @param mapstructMapper        the MapStruct mapper
     * @param conversionServiceMapper the ConversionService mapper
     * @return the selected {@link ProductConverter} implementation
     */
    @Bean
    @Primary
    public ProductConverter productConverterSelector(
            MapstructProductMapper mapstructMapper,
            ConversionServiceProductConverter conversionServiceMapper
    ) {

        return switch (type) {
            case "mapstruct" -> mapstructMapper;
            case "conversion-service" -> conversionServiceMapper;
            default -> throw new IllegalArgumentException("Unknown mapper type: " + type);
        };
    }

}
