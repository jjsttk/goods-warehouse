package com.jjsttk.goodswarehouse.mapper.converter;

import com.jjsttk.goodswarehouse.service.product.command.ProductServiceCreateCommand;
import com.jjsttk.goodswarehouse.persistence.entity.ProductEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public final class ProductServiceCreateCommandToProductEntityConverter
        implements Converter<ProductServiceCreateCommand, ProductEntity> {

    @Override
    public ProductEntity convert(ProductServiceCreateCommand source) {
        return ProductEntity.builder()
                .name(source.getName())
                .article(source.getArticle())
                .category(source.getCategory())
                .quantity(source.getQuantity())
                .price(source.getPrice())
                .description(source.getDescription())
                .build();
    }
}
