package com.jjsttk.goodswarehouse.service.order.product;

import com.jjsttk.goodswarehouse.service.order.product.dto.response.OrderProductServiceProductSummary;
import com.jjsttk.goodswarehouse.service.order.product.dto.response.OrderProductServiceResponseContainer;

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
    OrderProductServiceResponseContainer<OrderProductServiceProductSummary> getOrderedProducts(
            UUID orderId
    );
}
