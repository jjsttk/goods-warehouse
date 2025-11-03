package com.jjsttk.goodswarehouse.mapper.converter;

import com.jjsttk.goodswarehouse.controller.request.CreateProductRequest;
import com.jjsttk.goodswarehouse.service.product.command.ProductServiceCreateCommand;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public final class CreateProductRequestToProductServiceCreateCommandConverter
        implements Converter<CreateProductRequest, ProductServiceCreateCommand> {

    @Override
    public ProductServiceCreateCommand convert(CreateProductRequest source) {
        return ProductServiceCreateCommand.builder()
                .name(source.getName())
                .price(source.getPrice())
                .description(source.getDescription())
                .quantity(source.getQuantity())
                .article(source.getArticle())
                .category(source.getCategory())
                .build();
    }
}
