package com.jjsttk.goodswarehouse.mapper.converter;

import com.jjsttk.goodswarehouse.dto.response.ProductResponseDto;
import com.jjsttk.goodswarehouse.model.entity.Product;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public final class ProductToProductResponseDtoConverter implements Converter<Product, ProductResponseDto> {

    @Override
    public ProductResponseDto convert(Product source) {
        return ProductResponseDto.builder()
                .id(source.getId())
                .name(source.getName())
                .article(source.getArticle())
                .category(source.getCategory().name())
                .quantity(source.getQuantity())
                .price(source.getPrice())
                .description(source.getDescription())
                .createdAt(source.getCreatedAt())
                .lastQuantityModified(source.getLastQuantityModified())
                .build();
    }
}
