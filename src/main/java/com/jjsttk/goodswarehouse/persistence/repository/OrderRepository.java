package com.jjsttk.goodswarehouse.persistence.repository;

import com.jjsttk.goodswarehouse.persistence.entity.order.OrderEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
}
