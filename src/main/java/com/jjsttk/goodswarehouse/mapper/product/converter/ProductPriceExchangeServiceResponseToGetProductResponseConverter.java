package com.jjsttk.goodswarehouse.mapper.product.converter;

import com.jjsttk.goodswarehouse.controller.product.dto.response.GetProductResponse;
import com.jjsttk.goodswarehouse.service.product.price.exchange.dto.response.ProductPriceExchangeServiceResponse;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public final class ProductPriceExchangeServiceResponseToGetProductResponseConverter
        implements Converter<ProductPriceExchangeServiceResponse, GetProductResponse> {

    @Override
    public GetProductResponse convert(ProductPriceExchangeServiceResponse source) {
        return GetProductResponse.builder()
                .id(source.id())
                .name(source.name())
                .article(source.article())
                .description(source.description())
                .category(source.category())
                .price(source.price())
                .quantity(source.quantity())
                .currency(source.currency())
                .lastQuantityModified(source.lastQuantityModified())
                .createdAt(source.createdAt())
                .build();
    }
}
