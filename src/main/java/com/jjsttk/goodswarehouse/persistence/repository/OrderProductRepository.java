package com.jjsttk.goodswarehouse.persistence.repository;

import com.jjsttk.goodswarehouse.persistence.entity.order.product.OrderProductEntity;
import com.jjsttk.goodswarehouse.service.order.product.dto.response.OrderProductServiceProductSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderProductRepository extends JpaRepository<OrderProductEntity, UUID> {


    @Query("SELECT product.id as productId, "
           + "product.name as name, "
           + "orderProduct.orderedQuantity as quantity, "
           + "orderProduct.price as price "
           + "FROM OrderProductEntity orderProduct "
           + "JOIN orderProduct.product product "
           + "WHERE orderProduct.order.id = :orderId")
    List<OrderProductServiceProductSummary> findProductSummariesByOrderId(UUID orderId);
}
