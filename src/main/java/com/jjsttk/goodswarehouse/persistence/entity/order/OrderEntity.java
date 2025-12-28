package com.jjsttk.goodswarehouse.persistence.entity.order;

import com.jjsttk.goodswarehouse.persistence.entity.BaseEntity;
import com.jjsttk.goodswarehouse.persistence.entity.customer.CustomerEntity;
import com.jjsttk.goodswarehouse.persistence.entity.order.product.OrderProductEntity;
import com.jjsttk.goodswarehouse.shared.enums.order.OrderStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "\"order\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class OrderEntity implements BaseEntity {

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "customer_id",
            referencedColumnName = "id",
            nullable = false,
            updatable = false
    )
    private CustomerEntity customer;

    @Column(name = "status", nullable = false)
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.CREATED;

    @Column(name = "delivery_address", nullable = false)
    private String deliveryAddress;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<OrderProductEntity> orderProducts = new ArrayList<>();

    /**
     * Associates a collection of order products with this order entity and establishes the bidirectional relationship.
     * <p>
     * This method adds the provided products to the {@code orderProducts} list and sets
     * this {@code OrderEntity} as the {@code order} field within each {@code OrderProductEntity}.
     * This is crucial for maintaining the consistency of the parent-child relationship in JPA.
     *
     * @param products The collection of {@link OrderProductEntity} instances to add to the order.
     *                 If the collection is null or empty, the method does nothing.
     */
    public void addOrderProducts(Collection<OrderProductEntity> products) {
        if (products != null && !products.isEmpty()) {
            this.orderProducts.addAll(products);
            products.forEach(orderProduct -> orderProduct.setOrder(this));
        }
    }
}
