package com.jjsttk.goodswarehouse.service.order;

import com.jjsttk.goodswarehouse.exception.service.ResourceNotFoundException;
import com.jjsttk.goodswarehouse.exception.service.customer.CustomerBannedException;
import com.jjsttk.goodswarehouse.exception.service.order.NotEnoughQuantityInStockException;
import com.jjsttk.goodswarehouse.exception.service.order.NotYourOrderException;
import com.jjsttk.goodswarehouse.exception.service.order.OrderCannotBeCancelledException;
import com.jjsttk.goodswarehouse.exception.service.order.OrderCannotBeUpdatedException;
import com.jjsttk.goodswarehouse.exception.service.order.product.ProductsToOrderNotFoundException;
import com.jjsttk.goodswarehouse.service.order.dto.command.OrderServiceCreateCommand;
import com.jjsttk.goodswarehouse.service.order.dto.command.OrderServiceUpdateCommand;
import com.jjsttk.goodswarehouse.service.order.dto.response.BaseOrderServiceResponse;
import com.jjsttk.goodswarehouse.shared.enums.order.OrderStatus;

import java.util.UUID;

/**
 * Service interface for order management operations.
 * Provides methods for order lifecycle management including creation,
 * retrieval, updates, and status changes.
 */
public interface OrderService {

    /**
     * Retrieves an order by its unique identifier.
     *
     * @param customerId ID of the customer making the request
     * @param orderId    UUID of the order to retrieve
     * @return order service response containing order details
     * @throws ResourceNotFoundException if no order is found with the given UUID
     * @throws NotYourOrderException     if customerId header not equals to Order.customer.id
     */
    BaseOrderServiceResponse getById(Long customerId, UUID orderId);

    /**
     * Creates a new order in the database.
     *
     * @param customerId                ID of the customer creating the order
     * @param orderServiceCreateCommand request DTO containing order creation data
     * @return order service response containing the created order details
     * @throws CustomerBannedException           if customer is inactive
     * @throws NotEnoughQuantityInStockException if insufficient stock
     * @throws ProductsToOrderNotFoundException  if products not found
     */
    UUID create(Long customerId, OrderServiceCreateCommand orderServiceCreateCommand);

    /**
     * Updates an existing order with new values.
     * <p>
     * Only non-null fields from the DTO will be updated. Customer can only
     * update their own orders.
     * </p>
     *
     * @param customerId    ID of the customer making the update request
     * @param updateCommand DTO containing update data
     * @return updated order in service response format
     * @throws ResourceNotFoundException         if the order is not found
     * @throws NotYourOrderException       if customer tries to update another customer's order
     * @throws OrderCannotBeUpdatedException     if order status is not equal CREATED
     * @throws NotEnoughQuantityInStockException if selected product don`t have needed quantity in stock
     */

    UUID update(Long customerId, OrderServiceUpdateCommand updateCommand);

    /**
     * Cancels an existing order.
     * <p>
     * Marks the order as cancelled and releases any reserved inventory.
     * Customer can only cancel their own orders.
     * </p>
     *
     * @param customerId ID of the customer cancelling the order
     * @param orderId    UUID of the order to cancel
     * @throws ResourceNotFoundException       if order with the given UUID does not exist
     * @throws NotYourOrderException           if order.customer.id not equal to customerId from header
     * @throws OrderCannotBeCancelledException if order status is not equal to CREATED
     */
    void cancel(Long customerId, UUID orderId);

    /**
     * Confirms an order for processing.
     * <p>
     * Moves order from CREATED to CONFIRMED status, indicating it's ready
     * for fulfillment. Customer can only confirm their own orders.
     * </p>
     *
     * @param customerId ID of the customer confirming the order
     * @param orderId    UUID of the order to confirm
     */
    void confirm(Long customerId, UUID orderId);

    /**
     * Updates the status of an order.
     * <p>
     * System-level method for status transitions. Typically used by
     * administrators or automated processes.
     * </p>
     *
     * @param customerId customer id from header
     * @param orderId UUID of the order to update
     * @param status  new status to set
     * @throws ResourceNotFoundException       if order not found
     * @throws NotYourOrderException           if customer doesn't own the order
     */
    void updateOrderStatus(Long customerId, UUID orderId, OrderStatus status);
}
