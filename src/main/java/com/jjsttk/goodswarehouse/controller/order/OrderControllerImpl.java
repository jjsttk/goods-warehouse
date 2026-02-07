package com.jjsttk.goodswarehouse.controller.order;

import com.jjsttk.goodswarehouse.controller.order.dto.request.create.OrderCreateRequest;
import com.jjsttk.goodswarehouse.controller.order.dto.request.update.product.OrderProductUpdateRequest;
import com.jjsttk.goodswarehouse.controller.order.dto.request.update.status.OrderUpdateStatusRequest;
import com.jjsttk.goodswarehouse.controller.order.dto.response.GetOrderResponse;
import com.jjsttk.goodswarehouse.controller.order.dto.response.OrderInfo;
import com.jjsttk.goodswarehouse.mapper.order.OrderControllerConverter;
import com.jjsttk.goodswarehouse.service.exchange.ExchangeRateService;
import com.jjsttk.goodswarehouse.service.order.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(
        name = "Orders",
        description = "CRUD operations for warehouse orders"
)
public class OrderControllerImpl implements OrderController {
    private final OrderService orderService;
    private final OrderControllerConverter mapper;
    private final ExchangeRateService exchangeRateService;

    /**
     * {@inheritDoc}
     */

    @Override
    @GetMapping("/{orderId}")
    public GetOrderResponse getOrderById(
            @RequestHeader(name = "customerId") Long customerId,
            @PathVariable UUID orderId
    ) {
        var orderServiceResponse = orderService.getById(customerId, orderId);
        var sessionCurrencyRate = exchangeRateService.getCurrentSessionExchangeRate();

        return mapper.toResponse(orderServiceResponse, sessionCurrencyRate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @GetMapping("/by-products")
    public Map<UUID, List<OrderInfo>> getProductOrdersByProductId(@RequestParam List<UUID> ids) {
        return orderService.getOrdersDetailedInfosByProductIds(ids);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UUID createOrder(
            @RequestHeader(name = "customerId") Long customerId,
            @Valid @RequestBody OrderCreateRequest request
    ) {
        var createOrderCommandInfo = mapper.toServiceCommand(request);

        return orderService.create(customerId, createOrderCommandInfo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PatchMapping("/{orderId}")
    public UUID updateOrderById(
            @RequestHeader(name = "customerId") Long customerId,
            @PathVariable UUID orderId,
            @Valid @RequestBody List<OrderProductUpdateRequest> request
    ) {
        var updateOrderCommandInfo = mapper.toServiceCommand(orderId, request);

        return orderService.update(customerId, updateOrderCommandInfo);
    }

    /**
     * {@inheritDoc}
     * SoftDelete way
     */
    @Override
    @DeleteMapping("/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelOrderById(
            @RequestHeader Long customerId,
            @PathVariable UUID orderId
    ) {
        orderService.cancel(customerId, orderId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PostMapping("/{orderId}/confirm")
    public void confirmOrder(
            @RequestHeader(name = "customerId") Long customerId,
            @PathVariable UUID orderId
    ) {
        orderService.confirm(customerId, orderId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PatchMapping("/{orderId}/status")
    public void updateOrderStatus(
            @PathVariable UUID orderId,
            @RequestBody OrderUpdateStatusRequest orderUpdateStatusRequest
    ) {
        orderService.updateOrderStatus(orderId, orderUpdateStatusRequest.status());
    }
}
