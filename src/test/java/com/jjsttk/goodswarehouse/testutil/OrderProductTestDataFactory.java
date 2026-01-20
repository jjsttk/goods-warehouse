package com.jjsttk.goodswarehouse.testutil;

import com.jjsttk.goodswarehouse.persistence.entity.order.OrderEntity;
import com.jjsttk.goodswarehouse.persistence.entity.order.product.OrderProductEntity;
import com.jjsttk.goodswarehouse.persistence.entity.order.product.key.OrderProductId;
import com.jjsttk.goodswarehouse.persistence.entity.product.ProductEntity;
import com.jjsttk.goodswarehouse.service.order.product.dto.response.OrderProductProjection;
import com.jjsttk.goodswarehouse.service.order.product.dto.response.OrderProductResponseContainer;
import org.instancio.Instancio;

import java.math.BigDecimal;

import static org.instancio.Select.field;

public class OrderProductTestDataFactory {

    public static OrderProductEntity getOrderProductEntityWithoutOrderIdProductBased(
            ProductEntity product,
            BigDecimal orderedQuantity
    ) {
        return Instancio.of(OrderProductEntity.class)
                .set(field("id"), new OrderProductId(null, product.getId()))
                .set(field("product"), product)
                .set(field("orderedQuantity"), orderedQuantity)
                .ignore(field("order"))
                .create();
    }

    public static OrderProductEntity getOrderProductEntityWithoutOrderIdRandomProduct(
            BigDecimal orderedQuantity
    ) {
        var product = ProductTestDataFactory.getProductEntityWithGeneratedId();
        return Instancio.of(OrderProductEntity.class)
                .set(field("id"), new OrderProductId(null, product.getId()))
                .set(field("product"), product)
                .set(field("orderedQuantity"), orderedQuantity)
                .ignore(field("order"))
                .create();
    }

    public static OrderProductResponseContainer<OrderProductProjection> getServiceResponseBasedEntity(
            OrderEntity order
    ) {
        return OrderProductResponseContainer.<OrderProductProjection>builder()
                .orderProducts(order.getOrderProducts().stream()
                        .map(OrderProductTestDataFactory::getOrderProductServiceProductSummaryBasedOn)
                        .toList())
                .build();
    }

    public static OrderProductProjection getOrderProductServiceProductSummaryBasedOn(
            OrderProductEntity orderProduct
    ) {
        return Instancio.of(OrderProductProjection.class)
                .set(field("productId"), orderProduct.getId().getProductId())
                .set(field("name"), orderProduct.getProduct().getName())
                .set(field("quantity"), orderProduct.getOrderedQuantity())
                .set(field("price"), orderProduct.getPrice())
                .create();
    }
}
