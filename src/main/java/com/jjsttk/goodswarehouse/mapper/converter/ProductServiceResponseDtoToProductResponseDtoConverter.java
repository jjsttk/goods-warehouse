package com.jjsttk.goodswarehouse.mapper.converter;

import com.jjsttk.goodswarehouse.service.response.ProductServiceResponse;
import com.jjsttk.goodswarehouse.controller.response.GetProductResponse;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public final class ProductServiceResponseDtoToProductResponseDtoConverter
        implements Converter<ProductServiceResponse, GetProductResponse> {

    @Override
    public GetProductResponse convert(ProductServiceResponse source) {
        return GetProductResponse.builder()
                .id(source.id())
                .name(source.name())
                .article(source.article())
                .category(source.category())
                .quantity(source.quantity())
                .price(source.price())
                .description(source.description())
                .createdAt(source.createdAt())
                .lastQuantityModified(source.lastQuantityModified())
                .build();
    }
}
