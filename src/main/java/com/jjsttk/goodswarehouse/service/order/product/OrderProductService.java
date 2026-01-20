package com.jjsttk.goodswarehouse.service.order.product;

import com.jjsttk.goodswarehouse.service.order.product.dto.response.OrderProductProjection;
import com.jjsttk.goodswarehouse.service.order.product.dto.response.OrderProductResponseContainer;

import java.util.UUID;

/**
 * Service interface for managing order products.
 * Handles operations related to products within orders, including
 * retrieval, creation preparations, and updates.
 */
public interface OrderProductService {

    /**
     * Retrieves all products associated with an order.
     *
     * @param orderId UUID of the order to retrieve products for
     * @return container with list of order product summaries
     */
    OrderProductResponseContainer<OrderProductProjection> getOrderedProducts(
            UUID orderId
    );
}
