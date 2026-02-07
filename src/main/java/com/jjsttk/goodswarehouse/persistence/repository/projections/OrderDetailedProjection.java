package com.jjsttk.goodswarehouse.persistence.repository.projections;

import com.jjsttk.goodswarehouse.shared.enums.order.OrderStatus;

import java.util.UUID;

public interface OrderDetailedProjection {
    UUID getId();

    CustomerProjection getCustomer();

    OrderStatus getStatus();

    String getDeliveryAddress();

    OrderProductMinimalProjection getRequestedProduct();
}
