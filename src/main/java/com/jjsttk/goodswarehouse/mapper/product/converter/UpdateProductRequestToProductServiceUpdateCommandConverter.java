package com.jjsttk.goodswarehouse.mapper.product.converter;

import com.jjsttk.goodswarehouse.service.product.dto.command.ProductServiceUpdateCommand;
import com.jjsttk.goodswarehouse.controller.product.dto.request.UpdateProductRequest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public final class UpdateProductRequestToProductServiceUpdateCommandConverter
        implements Converter<UpdateProductRequest, ProductServiceUpdateCommand> {

    @Override
    public ProductServiceUpdateCommand convert(UpdateProductRequest source) {
        return ProductServiceUpdateCommand.builder()
                .name(source.getName())
                .price(source.getPrice())
                .description(source.getDescription())
                .article(source.getArticle())
                .category(source.getCategory())
                .quantity(source.getQuantity())
                .build();
    }
}
