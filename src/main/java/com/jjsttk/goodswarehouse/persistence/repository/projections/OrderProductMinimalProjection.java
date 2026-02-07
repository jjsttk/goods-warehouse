package com.jjsttk.goodswarehouse.persistence.repository.projections;

import com.jjsttk.goodswarehouse.persistence.entity.order.product.key.OrderProductId;

import java.math.BigDecimal;

public interface OrderProductMinimalProjection {
    OrderProductId getId();
    BigDecimal getOrderedQuantity();
}
