package com.jjsttk.goodswarehouse.service.order;

import com.jjsttk.goodswarehouse.controller.order.dto.response.OrderInfo;
import com.jjsttk.goodswarehouse.exception.service.ResourceNotFoundException;
import com.jjsttk.goodswarehouse.exception.service.customer.CustomerBannedException;
import com.jjsttk.goodswarehouse.exception.service.order.NotYourOrderException;
import com.jjsttk.goodswarehouse.exception.service.order.OrderCannotBeCancelledException;
import com.jjsttk.goodswarehouse.exception.service.order.OrderCannotBeUpdatedException;
import com.jjsttk.goodswarehouse.exception.service.product.ReservationException;
import com.jjsttk.goodswarehouse.service.order.dto.command.CreateOrderCommandInfo;
import com.jjsttk.goodswarehouse.service.order.dto.command.UpdateOrderCommandInfo;
import com.jjsttk.goodswarehouse.service.order.dto.response.BaseOrderResponse;
import com.jjsttk.goodswarehouse.shared.enums.order.OrderStatus;

import java.util.List;
import java.util.Map;
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
    BaseOrderResponse getById(Long customerId, UUID orderId);

    /**
     * Creates a new order in the database.
     *
     * @param customerId             ID of the customer creating the order
     * @param createOrderCommandInfo request DTO containing order creation data
     * @return order service response containing the created order details
     * @throws CustomerBannedException if customer is inactive
     * @throws ReservationException    if troubles while reserve products
     */
    UUID create(Long customerId, CreateOrderCommandInfo createOrderCommandInfo);

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
     * @throws ResourceNotFoundException     if the order is not found
     * @throws NotYourOrderException         if customer tries to update another customer's order
     * @throws OrderCannotBeUpdatedException if order status is not equal CREATED
     * @throws ReservationException          if troubles while reserve products
     */

    UUID update(Long customerId, UpdateOrderCommandInfo updateCommand);

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
     * @param orderId UUID of the order to update
     * @param status  new status to set
     * @throws ResourceNotFoundException if order not found
     * @throws NotYourOrderException     if customer doesn't own the order
     */
    void updateOrderStatus(UUID orderId, OrderStatus status);

    /**
     * Provides comprehensive information about orders that use each product from the provided list.
     * @param productIds UUIDs of interested products
     * @return {@code HashMap} where:
     * {@code KEY} is productId from productIds, {@code Value} is a list of DTO objects,
     * each of which contains information about orders that contain the required id from the key
     */
    Map<UUID, List<OrderInfo>> getOrdersDetailedInfosByProductIds(List<UUID> productIds);
}
