package com.jjsttk.goodswarehouse.mapper.converter;

import com.jjsttk.goodswarehouse.controller.request.CreateProductRequest;
import com.jjsttk.goodswarehouse.service.command.ProductCreateCommand;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public final class ProductRequestCreateDtoToProductCreateCommandConverter
        implements Converter<CreateProductRequest, ProductCreateCommand> {

    @Override
    public ProductCreateCommand convert(CreateProductRequest source) {
        return ProductCreateCommand.builder()
                .name(source.getName())
                .price(source.getPrice())
                .description(source.getDescription())
                .quantity(source.getQuantity())
                .article(source.getArticle())
                .category(source.getCategory())
                .build();
    }
}
