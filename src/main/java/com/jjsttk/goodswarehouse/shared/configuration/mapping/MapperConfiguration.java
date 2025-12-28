package com.jjsttk.goodswarehouse.shared.configuration.mapping;

import com.jjsttk.goodswarehouse.mapper.order.OrderControllerConverter;
import com.jjsttk.goodswarehouse.mapper.order.OrderServiceConverter;
import com.jjsttk.goodswarehouse.mapper.order.mapstruct.MapstructOrderControllerMapper;
import com.jjsttk.goodswarehouse.mapper.order.mapstruct.MapstructOrderServiceMapper;
import com.jjsttk.goodswarehouse.mapper.order.product.OrderProductConverter;
import com.jjsttk.goodswarehouse.mapper.order.product.mapstruct.MapstructOrderProductMapper;
import com.jjsttk.goodswarehouse.mapper.order.product.spring.ConversionServiceOrderProductMapper;
import com.jjsttk.goodswarehouse.mapper.order.spring.ConversionServiceOrderControllerMapper;
import com.jjsttk.goodswarehouse.mapper.order.spring.ConversionServiceOrderServiceMapper;
import com.jjsttk.goodswarehouse.mapper.product.ProductControllerConverter;
import com.jjsttk.goodswarehouse.mapper.product.ProductReservationConverter;
import com.jjsttk.goodswarehouse.mapper.product.ProductServiceConverter;
import com.jjsttk.goodswarehouse.mapper.product.mapstruct.MapstructProductControllerMapper;
import com.jjsttk.goodswarehouse.mapper.product.mapstruct.MapstructProductReservationMapper;
import com.jjsttk.goodswarehouse.mapper.product.mapstruct.MapstructProductServiceMapper;
import com.jjsttk.goodswarehouse.mapper.product.spring.ConversionServiceProductControllerConverter;
import com.jjsttk.goodswarehouse.mapper.product.spring.ConversionServiceProductReservationConverter;
import com.jjsttk.goodswarehouse.mapper.product.spring.ConversionServiceProductServiceConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MapperConfiguration {

    @Value("${app.mapper.type:conversion-service}")
    private String mapperType;

    // --- Order Mappers ---
    /**
     * Provides the primary {@link OrderServiceConverter} bean.
     * Chooses between MapStruct and ConversionService implementations.
     *
     * @param mapstruct        the MapStruct mapper
     * @param conversionService the ConversionService mapper
     * @return the selected {@link OrderServiceConverter} implementation
     */
    @Bean
    @Primary
    public OrderServiceConverter orderServiceConverter(
            MapstructOrderServiceMapper mapstruct,
            ConversionServiceOrderServiceMapper conversionService) {
        return selectMapper(mapstruct, conversionService);
    }

    /**
     * Provides the primary {@link OrderControllerConverter} bean.
     * Chooses between MapStruct and ConversionService implementations.
     *
     * @param mapstruct        the MapStruct mapper
     * @param conversionService the ConversionService mapper
     * @return the selected {@link OrderControllerConverter} implementation
     */
    @Bean
    @Primary
    public OrderControllerConverter orderControllerConverter(
            MapstructOrderControllerMapper mapstruct,
            ConversionServiceOrderControllerMapper conversionService) {
        return selectMapper(mapstruct, conversionService);
    }

    // --- OrderProduct Mappers ---
    /**
     * Provides the primary {@link OrderProductConverter} bean.
     * Chooses between MapStruct and ConversionService implementations.
     *
     * @param mapstruct        the MapStruct mapper
     * @param conversionService the ConversionService mapper
     * @return the selected {@link OrderProductConverter} implementation
     */
    @Bean
    @Primary
    public OrderProductConverter orderProductConverter(
            MapstructOrderProductMapper mapstruct,
            ConversionServiceOrderProductMapper conversionService) {
        return selectMapper(mapstruct, conversionService);
    }

    // --- Product Mappers ---
    /**
     * Provides the primary {@link ProductServiceConverter} bean.
     * Chooses between MapStruct and ConversionService implementations.
     *
     * @param mapstruct        the MapStruct mapper
     * @param conversionService the ConversionService mapper
     * @return the selected {@link ProductServiceConverter} implementation
     */
    @Bean
    @Primary
    public ProductServiceConverter productServiceConverter(
            MapstructProductServiceMapper mapstruct,
            ConversionServiceProductServiceConverter conversionService) {
        return selectMapper(mapstruct, conversionService);
    }

    /**
     * Provides the primary {@link ProductControllerConverter} bean.
     * Chooses between MapStruct and ConversionService implementations.
     *
     * @param mapstruct        the MapStruct mapper
     * @param conversionService the ConversionService mapper
     * @return the selected {@link ProductControllerConverter} implementation
     */
    @Bean
    @Primary
    public ProductControllerConverter productControllerConverter(
            MapstructProductControllerMapper mapstruct,
            ConversionServiceProductControllerConverter conversionService) {
        return selectMapper(mapstruct, conversionService);
    }

    /**
     * Provides the primary {@link ProductReservationConverter} bean.
     * Chooses between MapStruct and ConversionService implementations.
     *
     * @param mapstruct        the MapStruct mapper
     * @param conversionService the ConversionService mapper
     * @return the selected {@link ProductReservationConverter} implementation
     */
    @Bean
    @Primary
    public ProductReservationConverter productReservationConverter(
            MapstructProductReservationMapper mapstruct,
            ConversionServiceProductReservationConverter conversionService) {
        return selectMapper(mapstruct, conversionService);
    }


    /**
     *
     * @param mapstructMapper generic MapstructMapper object
     * @param conversionServiceMapper generic ConversionServiceMapper object
     * @return Mapstruct or ConversionService bean based on config path {app.mapper.type:conversion-service}
     * @param <T> The common interface from which mappers are inherited
     */
    private <T> T selectMapper(T mapstructMapper, T conversionServiceMapper) {
        return switch (mapperType.toLowerCase()) {
            case "mapstruct" -> mapstructMapper;
            case "conversion-service" -> conversionServiceMapper;
            default -> throw new IllegalArgumentException(
                    "Unknown mapper type: " + mapperType + ". Supported: mapstruct, conversion-service"
            );
        };
    }
}
