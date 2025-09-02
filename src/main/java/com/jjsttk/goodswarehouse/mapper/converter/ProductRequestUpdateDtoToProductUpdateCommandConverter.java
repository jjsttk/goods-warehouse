package com.jjsttk.goodswarehouse.mapper.converter;

import com.jjsttk.goodswarehouse.service.command.ProductUpdateCommand;
import com.jjsttk.goodswarehouse.controller.request.UpdateProductRequest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public final class ProductRequestUpdateDtoToProductUpdateCommandConverter
        implements Converter<UpdateProductRequest, ProductUpdateCommand> {

    @Override
    public ProductUpdateCommand convert(UpdateProductRequest source) {
        return ProductUpdateCommand.builder()
                .name(source.getName())
                .price(source.getPrice())
                .description(source.getDescription())
                .article(source.getArticle())
                .category(source.getCategory())
                .quantity(source.getQuantity())
                .build();
    }
}
