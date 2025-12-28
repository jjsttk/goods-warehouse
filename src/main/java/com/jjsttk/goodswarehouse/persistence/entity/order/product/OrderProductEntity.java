package com.jjsttk.goodswarehouse.persistence.entity.order.product;

import com.jjsttk.goodswarehouse.persistence.entity.BaseEntity;
import com.jjsttk.goodswarehouse.persistence.entity.order.OrderEntity;
import com.jjsttk.goodswarehouse.persistence.entity.order.product.key.OrderProductId;
import com.jjsttk.goodswarehouse.persistence.entity.product.ProductEntity;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "order_product")
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class OrderProductEntity implements BaseEntity {

    @EmbeddedId
    @Builder.Default
    private OrderProductId id = new OrderProductId();

    @MapsId("orderId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "order_id",
            referencedColumnName = "id",
            nullable = false,
            updatable = false
    )
    private OrderEntity order;

    @MapsId("productId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "product_id",
            referencedColumnName = "id",
            nullable = false,
            updatable = false
    )
    private ProductEntity product;

    @Column(name = "ordered_quantity", nullable = false)
    private BigDecimal orderedQuantity;

    @Column(name = "price", nullable = false)
    private BigDecimal price;
}
