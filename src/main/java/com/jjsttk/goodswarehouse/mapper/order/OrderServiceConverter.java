package com.jjsttk.goodswarehouse.mapper.order;

import com.jjsttk.goodswarehouse.controller.order.dto.response.OrderInfo;
import com.jjsttk.goodswarehouse.controller.order.dto.response.customer.CustomerInfo;
import com.jjsttk.goodswarehouse.persistence.entity.order.OrderEntity;
import com.jjsttk.goodswarehouse.persistence.repository.projections.CustomerProjection;
import com.jjsttk.goodswarehouse.persistence.repository.projections.OrderDetailedProjection;
import com.jjsttk.goodswarehouse.service.order.dto.internal.CustomerExternalData;
import com.jjsttk.goodswarehouse.service.order.dto.internal.CustomerExternalDataInfo;
import com.jjsttk.goodswarehouse.service.order.dto.response.BaseOrderResponse;
import com.jjsttk.goodswarehouse.persistence.repository.projections.OrderProductSummaryProjection;
import com.jjsttk.goodswarehouse.service.order.product.dto.response.OrderProductResponseContainer;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public interface OrderServiceConverter {
    BaseOrderResponse toResponse(
            UUID orderId,
            OrderProductResponseContainer<OrderProductSummaryProjection> serviceResponse
    );

    OrderEntity toEntity(Long customerId, String deliveryAddress);

    default Map<UUID, List<OrderInfo>> toResponse(
            Map<UUID, List<OrderDetailedProjection>> orderIdToOrderInfoMap,
            CustomerExternalData customerExternalData
    ) {
        return orderIdToOrderInfoMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream()
                                .map(it -> {
                                    var customerId = it.getCustomer().getId();
                                    var customerExternalDataInfo = customerExternalData.infoMap().get(customerId);

                                    return toOrderInfo(it, customerExternalDataInfo);
                                })
                                .toList()
                ));
    }

    private OrderInfo toOrderInfo(
            OrderDetailedProjection src,
            CustomerExternalDataInfo customerExternalDataInfo
    ) {
        return OrderInfo.builder()
                .id(src.getId())
                .status(src.getStatus())
                .quantity(src.getRequestedProduct().getOrderedQuantity())
                .deliveryAddress(src.getDeliveryAddress())
                .customer(toCustomerInfo(src.getCustomer(), customerExternalDataInfo))
                .build();
    }

    private CustomerInfo toCustomerInfo(
            CustomerProjection src,
            CustomerExternalDataInfo enrichment
    ) {
        return CustomerInfo.builder()
                .id(src.getId())
                .email(src.getEmail())
                .inn(enrichment.inn())
                .accountNumber(enrichment.accNum())
                .build();
    }
}
