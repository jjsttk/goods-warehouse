package com.jjsttk.goodswarehouse.mapper.order.product.spring;

import com.jjsttk.goodswarehouse.mapper.order.product.OrderProductConverter;
import com.jjsttk.goodswarehouse.mapper.util.converter.ReferenceConverter;
import com.jjsttk.goodswarehouse.persistence.entity.order.product.OrderProductEntity;
import com.jjsttk.goodswarehouse.persistence.entity.product.ProductEntity;
import com.jjsttk.goodswarehouse.service.product.dto.response.ReservedProductInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public final class ConversionServiceOrderProductMapper implements OrderProductConverter {
    private final ConversionService conversionService;
    private final ReferenceConverter referenceConverter;

    @Override
    public OrderProductEntity toEntity(
            UUID productId,
            ReservedProductInfo productInfo
    ) {

        return OrderProductEntity.builder()
                .product(referenceConverter.toEntity(productId, ProductEntity.class))
                .price(productInfo.priceAtMoment())
                .orderedQuantity(productInfo.reservedQuantity())
                .build();
    }

    @Override
    public void update(
            OrderProductEntity orderProductEntity,
            ReservedProductInfo updateInfo
    ) {
        var currentQuantity = orderProductEntity.getOrderedQuantity();
        var deltaQuantity = updateInfo.reservedQuantity();

        orderProductEntity.setOrderedQuantity(currentQuantity.add(deltaQuantity));
        orderProductEntity.setPrice(updateInfo.priceAtMoment());
    }
}
