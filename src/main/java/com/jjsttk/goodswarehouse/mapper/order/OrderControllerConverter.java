package com.jjsttk.goodswarehouse.mapper.order;

import com.jjsttk.goodswarehouse.controller.order.dto.request.create.OrderCreateRequest;
import com.jjsttk.goodswarehouse.controller.order.dto.request.update.product.OrderProductUpdateRequest;
import com.jjsttk.goodswarehouse.controller.order.dto.response.GetOrderResponse;
import com.jjsttk.goodswarehouse.service.exchange.dto.response.ExchangeRate;
import com.jjsttk.goodswarehouse.service.order.dto.command.CreateOrderCommandInfo;
import com.jjsttk.goodswarehouse.service.order.dto.command.UpdateOrderCommandInfo;
import com.jjsttk.goodswarehouse.service.order.dto.response.BaseOrderResponse;

import java.util.Collection;
import java.util.UUID;

public interface OrderControllerConverter {

    CreateOrderCommandInfo toServiceCommand(OrderCreateRequest createRequest);
    UpdateOrderCommandInfo toServiceCommand(UUID orderId, Collection<OrderProductUpdateRequest> request);
    GetOrderResponse toResponse(BaseOrderResponse orderServiceResponse, ExchangeRate exchangeRate);
}
