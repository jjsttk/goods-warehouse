package com.jjsttk.goodswarehouse.mapper.product.spring.converter;

import com.jjsttk.goodswarehouse.persistence.entity.product.ProductEntity;
import com.jjsttk.goodswarehouse.service.product.dto.response.ProductDetailedResponse;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public final class ProductEntityToProductDetailedResponseConverter
        implements Converter<ProductEntity, ProductDetailedResponse> {

    @Override
    public ProductDetailedResponse convert(ProductEntity source) {
        return ProductDetailedResponse.builder()
                .id(source.getId())
                .name(source.getName())
                .price(source.getPrice())
                .article(source.getArticle())
                .quantity(source.getQuantity())
                .category(source.getCategory())
                .createdAt(source.getCreatedAt())
                .description(source.getDescription())
                .isAvailable(source.getIsAvailable())
                .lastQuantityModified(source.getLastQuantityModified())
                .build();
    }
}
