package com.jjsttk.goodswarehouse.mapper.converter;

import com.jjsttk.goodswarehouse.service.product.response.BaseProductServiceDto;
import com.jjsttk.goodswarehouse.persistence.entity.ProductEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public final class ProductEntityToBaseProductServiceDtoConverter
        implements Converter<ProductEntity, BaseProductServiceDto> {

    @Override
    public BaseProductServiceDto convert(ProductEntity source) {
        return BaseProductServiceDto.builder()
                .id(source.getId())
                .name(source.getName())
                .price(source.getPrice())
                .article(source.getArticle())
                .description(source.getDescription())
                .category(source.getCategory())
                .quantity(source.getQuantity())
                .createdAt(source.getCreatedAt())
                .lastQuantityModified(source.getLastQuantityModified())
                .build();
    }
}
