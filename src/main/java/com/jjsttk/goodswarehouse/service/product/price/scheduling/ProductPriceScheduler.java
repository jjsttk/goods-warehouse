package com.jjsttk.goodswarehouse.service.product.price.scheduling;

/**
 * Common interface for all product price schedulers.
 * <p>
 * Implementations of this interface are responsible for periodically
 * increasing the prices of products in the system according to a defined strategy.
 * This abstraction allows switching between different scheduler implementations
 * (e.g., simple in-memory update vs. optimized database-level update)
 * </p>
 */
public interface ProductPriceScheduler {

    /**
     * Performs the price increase operation.
     * <p>
     * Implementations should handle the logic of calculating the new prices
     * and updating the corresponding products in the database.
     * </p>
     */
    void runTask();
}
