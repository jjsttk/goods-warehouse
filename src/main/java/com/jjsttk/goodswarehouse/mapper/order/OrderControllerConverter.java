package com.jjsttk.goodswarehouse.mapper.order;

import com.jjsttk.goodswarehouse.controller.order.dto.request.create.OrderCreateRequest;
import com.jjsttk.goodswarehouse.controller.order.dto.request.update.product.OrderProductUpdateRequest;
import com.jjsttk.goodswarehouse.controller.order.dto.response.GetOrderResponse;
import com.jjsttk.goodswarehouse.service.order.dto.command.OrderServiceCreateCommand;
import com.jjsttk.goodswarehouse.service.order.dto.command.OrderServiceUpdateCommand;
import com.jjsttk.goodswarehouse.service.order.price.dto.response.OrderPriceExchangeServiceResponse;

import java.util.Collection;
import java.util.UUID;

public interface OrderControllerConverter {

    OrderServiceCreateCommand toServiceCommand(OrderCreateRequest createRequest);
    OrderServiceUpdateCommand toServiceCommand(UUID orderId, Collection<OrderProductUpdateRequest> request);
    GetOrderResponse toResponse(OrderPriceExchangeServiceResponse exchangeServiceResponse);
}
