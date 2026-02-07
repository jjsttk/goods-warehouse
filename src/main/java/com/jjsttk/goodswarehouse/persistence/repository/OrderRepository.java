package com.jjsttk.goodswarehouse.persistence.repository;

import com.jjsttk.goodswarehouse.persistence.entity.order.OrderEntity;
import com.jjsttk.goodswarehouse.persistence.repository.projections.OrderDetailedProjection;
import com.jjsttk.goodswarehouse.shared.enums.order.OrderStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {


    @Query("""
                SELECT o
                FROM OrderEntity o
                JOIN FETCH o.customer c
                JOIN FETCH o.orderProducts op
                JOIN FETCH op.product
                WHERE o.id = :orderId
            """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<OrderEntity> findByIdForUpdate(UUID orderId);

    boolean existsByIdAndCustomerId(UUID orderId, Long customerId);

    @Query("""
            SELECT order.id as id,
            order.customer as customer,
            order.deliveryAddress as deliveryAddress,
            order.status as status,
            order.orderProducts as requestedProduct
            FROM OrderEntity order
            JOIN order.customer c
            JOIN order.orderProducts op
            WHERE op.id.productId in :productIds
            AND order.status in :orderStatuses
            """)
    List<OrderDetailedProjection> findAllByProductIdIn(List<UUID> productIds, List<OrderStatus> orderStatuses);
}
