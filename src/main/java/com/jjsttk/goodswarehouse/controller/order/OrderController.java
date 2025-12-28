package com.jjsttk.goodswarehouse.controller.order;

import com.jjsttk.goodswarehouse.controller.order.dto.request.create.OrderCreateRequest;
import com.jjsttk.goodswarehouse.controller.order.dto.request.update.product.OrderProductUpdateRequest;
import com.jjsttk.goodswarehouse.controller.order.dto.request.update.status.OrderUpdateStatusRequest;
import com.jjsttk.goodswarehouse.controller.order.dto.response.GetOrderResponse;
import com.jjsttk.goodswarehouse.exception.dto.response.ErrorResponse;
import com.jjsttk.goodswarehouse.exception.service.ResourceNotFoundException;
import com.jjsttk.goodswarehouse.exception.service.customer.CustomerBannedException;
import com.jjsttk.goodswarehouse.exception.service.order.NotYourOrderException;
import com.jjsttk.goodswarehouse.exception.service.order.OrderCannotBeCancelledException;
import com.jjsttk.goodswarehouse.exception.service.order.OrderCannotBeUpdatedException;
import com.jjsttk.goodswarehouse.exception.service.order.product.ProductsToOrderNotFoundException;
import com.jjsttk.goodswarehouse.exception.service.order.NotEnoughQuantityInStockException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.UUID;

/**
 * REST API interface for managing warehouse orders.
 * Provides endpoints for order CRUD operations with validation support.
 * All operations require customer authentication via customerId header.
 */
@Tag(
        name = "Order Management",
        description = "APIs for managing customer orders in the warehouse system"
)
public interface OrderController {

    /**
     * Retrieves an order by its unique ID.
     *
     * @param customerId ID of the authenticated customer (from header)
     * @param orderId    UUID of the order to retrieve
     * @return order details with products and pricing information
     * @throws ResourceNotFoundException if order not found
     * @throws NotYourOrderException     if customer doesn't own the order
     */
    @Operation(
            summary = "Get order by ID",
            description = "Returns detailed information about a specific order using its unique identifier. "
                          + "Customer can only retrieve their own orders.",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Order retrieved successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = GetOrderResponse.class))),
                    @ApiResponse(responseCode = "403",
                            description = "Access denied - customer tries to access another customer's order",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404",
                            description = "Order not found or cancelled",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500",
                            description = "Internal server error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping("/{orderId}")
    GetOrderResponse getOrderById(
            @Parameter(
                    description = "ID of the authenticated customer",
                    required = true,
                    example = "12345"
            )
            @RequestHeader(name = "customerId") Long customerId,
            @Parameter(
                    description = "UUID of the order to retrieve",
                    required = true,
                    example = "123e4567-e89b-12d3-a456-426614174000"
            )
            @PathVariable UUID orderId
    );

    /**
     * Creates a new order in the warehouse.
     *
     * @param customerId ID of the authenticated customer (from header)
     * @param request    order creation data including products and quantities
     * @return UUID of the created order
     * @throws CustomerBannedException           if customer is inactive
     * @throws NotEnoughQuantityInStockException if insufficient stock
     * @throws ProductsToOrderNotFoundException  if products not found
     */
    @Operation(
            summary = "Create a new order",
            description = "Creates a new order for the authenticated customer. "
                          + "Products are reserved immediately upon creation. "
                          + "Order starts in 'CREATED' status.",
            responses = {
                    @ApiResponse(responseCode = "201",
                            description = "Order created successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UUID.class))),
                    @ApiResponse(responseCode = "400",
                            description = "Validation failed or invalid input",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "403",
                            description = "Customer is banned/inactive",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404",
                            description = "One or more products not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "409",
                            description = "Insufficient stock quantity",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500",
                            description = "Internal server error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    UUID createOrder(
            @Parameter(
                    description = "ID of the authenticated customer",
                    required = true,
                    example = "12345"
            )
            @RequestHeader(name = "customerId") Long customerId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Order creation payload containing products and quantities",
                    required = true,
                    content = @Content(schema = @Schema(implementation = OrderCreateRequest.class))
            )
            @Valid @RequestBody OrderCreateRequest request);

    /**
     * Updates an existing order with new product quantities.
     *
     * @param customerId ID of the authenticated customer (from header)
     * @param orderId    UUID of the order to update
     * @param request    list of product updates with new quantities
     * @return UUID of the updated order
     * @throws NotYourOrderException             if customer doesn't own the order
     * @throws ResourceNotFoundException         if order not found, product to order not found
     * @throws OrderCannotBeUpdatedException     if order not in CREATED status
     * @throws NotEnoughQuantityInStockException if selected product don`t have needed quantity in stock
     */
    @Operation(
            summary = "Update an existing order",
            description = "Updates product quantities in an existing order. "
                          + "Only orders in 'CREATED' status can be updated. "
                          + "Supports adding new products, updating quantities, "
                          + "or removing products (by setting quantity to 0).",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Order updated successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UUID.class))),
                    @ApiResponse(responseCode = "400",
                            description = "Validation failed or invalid input",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "403",
                            description = "Customer tries to update another customer's order",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404",
                            description = "Order not found/Product from update not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "409",
                            description = "Order cannot be updated (wrong status)",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500",
                            description = "Internal server error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PatchMapping("/{orderId}")
    UUID updateOrderById(
            @Parameter(
                    description = "ID of the authenticated customer",
                    required = true,
                    example = "12345"
            )
            @RequestHeader(name = "customerId") Long customerId,

            @Parameter(
                    description = "UUID of the order to update",
                    required = true,
                    example = "123e4567-e89b-12d3-a456-426614174000"
            )
            @PathVariable UUID orderId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "List of product updates with product UUIDs and new quantities",
                    required = true,
                    content = @Content(schema = @Schema(implementation = OrderProductUpdateRequest.class))
            )
            @Valid @RequestBody List<OrderProductUpdateRequest> request);

    /**
     * Cancels an existing order.
     *
     * @param customerId ID of the authenticated customer (from header)
     * @param orderId    UUID of the order to cancel
     * @throws NotYourOrderException           if customer doesn't own the order
     * @throws ResourceNotFoundException       if order is not found
     * @throws OrderCannotBeCancelledException if order not in CREATED status
     */
    @Operation(
            summary = "Cancel order",
            description = "Cancels an existing order and returns reserved stock to inventory. "
                          + "Only orders in 'CREATED' status can be cancelled. "
                          + "Upon cancellation, order status changes to 'CANCELLED'.",
            responses = {
                    @ApiResponse(responseCode = "204",
                            description = "Order cancelled successfully"),
                    @ApiResponse(responseCode = "403",
                            description = "Customer tries to cancel another customer's order",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404",
                            description = "Order not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "409",
                            description = "Order cannot be cancelled (wrong status)",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500",
                            description = "Internal server error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @DeleteMapping("/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void cancelOrderById(
            @Parameter(
                    description = "ID of the authenticated customer",
                    required = true,
                    example = "12345"
            )
            @RequestHeader(name = "customerId") Long customerId,

            @Parameter(
                    description = "UUID of the order to cancel",
                    required = true,
                    example = "123e4567-e89b-12d3-a456-426614174000"
            )
            @PathVariable UUID orderId
    );

    /**
     * Confirms an order for processing.
     *
     * @param customerId ID of the authenticated customer (from header)
     * @param orderId    UUID of the order to confirm
     */
    @Operation(
            summary = "Confirm order",
            description = "TODO",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Order confirmed successfully"),
                    @ApiResponse(responseCode = "400",
                            description = "Invalid input",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "403",
                            description = "Access denied",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404",
                            description = "Order not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
            }
    )
    @PostMapping("/{orderId}/confirm")
    @ResponseStatus(HttpStatus.OK)
    void confirmOrder(
            @Parameter(
                    description = "ID of the authenticated customer",
                    required = true,
                    example = "12345"
            )
            @RequestHeader(name = "customerId") Long customerId,

            @Parameter(
                    description = "UUID of the order to confirm",
                    required = true,
                    example = "123e4567-e89b-12d3-a456-426614174000"
            )
            @PathVariable UUID orderId
    );

    /**
     * Updates the status of an existing order.
     *
     * @param orderId    UUID of the order to update status for
     * @param customerId ID of the authenticated customer (from header)
     * @param request    Status update data
     * @throws ResourceNotFoundException       if order not found
     * @throws NotYourOrderException           if customer doesn't own the order
     * @throws OrderCannotBeCancelledException if order cannot transition to the requested status
     */
    @Operation(
            summary = "Update order status",
            description = "Updates the status of an existing order. "
                          + "Products may be returned to stock depending on the new status (e.g., CANCELLED).",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Order status updated successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UUID.class))),
                    @ApiResponse(responseCode = "403",
                            description = "CustomerId header not equal to order.customer.id",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404",
                            description = "Order not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PatchMapping("/{orderId}/status")
    void updateOrderStatus(
            @Parameter(
                    description = "ID of the authenticated customer",
                    required = true,
                    example = "12345"
            )
            @RequestHeader(name = "customerId") Long customerId,
            @Parameter(
                    description = "UUID of the order to update status for",
                    required = true,
                    example = "123e4567-e89b-12d3-a456-426614174000"
            )
            @PathVariable UUID orderId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "New status for the order",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderUpdateStatusRequest.class))
            )
            @Valid @RequestBody OrderUpdateStatusRequest request
    );
}
