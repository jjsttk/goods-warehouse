package com.jjsttk.goodswarehouse.testutil;

import com.jjsttk.goodswarehouse.controller.order.dto.request.create.OrderCreateRequest;
import com.jjsttk.goodswarehouse.controller.order.dto.request.create.product.OrderProductCreateRequest;
import com.jjsttk.goodswarehouse.controller.order.dto.request.update.product.OrderProductUpdateRequest;
import com.jjsttk.goodswarehouse.controller.order.dto.response.GetOrderResponse;
import com.jjsttk.goodswarehouse.controller.order.dto.response.product.GetOrderProductResponse;
import com.jjsttk.goodswarehouse.persistence.entity.order.OrderEntity;
import com.jjsttk.goodswarehouse.service.order.dto.command.OrderServiceCreateCommand;
import com.jjsttk.goodswarehouse.service.order.dto.command.OrderServiceUpdateCommand;
import com.jjsttk.goodswarehouse.service.order.dto.response.BaseOrderServiceResponse;
import com.jjsttk.goodswarehouse.service.order.dto.response.OrderServiceProductInOrderResponse;
import com.jjsttk.goodswarehouse.service.order.product.dto.response.OrderProductServiceProductSummary;
import com.jjsttk.goodswarehouse.shared.enums.order.OrderStatus;
import org.instancio.Instancio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.instancio.Select.field;

public class OrderTestDataFactory {

    public static OrderEntity getOrderEntityWithIdByLengthAndStatus(
            int orderedProductsLength,
            OrderStatus status,
            BigDecimal orderedQuantityForAll,
            boolean isActiveCustomer
    ) {
        var orderId = UUID.randomUUID();
        var customer = CustomerTestDataFactory.getCustomerEntityWithGeneratedId(isActiveCustomer);
        var products = ProductTestDataFactory.getProductsListWithId(orderedProductsLength);
        var orderProducts = products.stream()
                .map(it -> {
                    var op =
                            OrderProductTestDataFactory.getOrderProductEntityWithoutOrderIdProductBased(
                                    it,
                                    orderedQuantityForAll
                            );
                    op.getId().setOrderId(orderId);

                    return op;
                })
                .toList();

        var orderEntity = Instancio.of(OrderEntity.class)
                .set(field("id"), orderId)
                .set(field("status"), status)
                .set(field("customer"), customer)
                .ignore(field("orderProducts"))
                .create();

        orderEntity.addOrderProducts(orderProducts);

        return orderEntity;
    }

    public static OrderEntity getOrderEntityWithoutIdAndCustomerIsActive(
            int orderedProductsLength,
            BigDecimal orderedQuantityForAll
    ) {
        var activeCustomer = CustomerTestDataFactory.getCustomerEntityWithGeneratedId(true);
        var products = ProductTestDataFactory.getProductsListWithId(orderedProductsLength);
        var orderProducts = products.stream()
                .map(it -> OrderProductTestDataFactory.getOrderProductEntityWithoutOrderIdProductBased(
                        it,
                        orderedQuantityForAll
                ))
                .toList();

        var orderEntity = Instancio.of(OrderEntity.class)
                .ignore(field("id"))
                .set(field("customer"), activeCustomer)
                .ignore(field("orderProducts"))
                .create();

        orderEntity.addOrderProducts(orderProducts);

        return orderEntity;
    }

    public static OrderServiceProductInOrderResponse getOrderServiceProductInOrderResponseBasedOn(
            OrderProductServiceProductSummary orderProductSummary
    ) {
        return Instancio.of(OrderServiceProductInOrderResponse.class)
                .set(field("productId"), orderProductSummary.productId())
                .set(field("name"), orderProductSummary.name())
                .set(field("quantity"), orderProductSummary.quantity())
                .set(field("price"), orderProductSummary.price())
                .create();
    }

    public static BaseOrderServiceResponse getBaseOrderServiceResponse(OrderEntity orderEntity) {
        return BaseOrderServiceResponse.builder()
                .orderId(orderEntity.getId())
                .products(orderEntity.getOrderProducts().stream()
                        .map(OrderProductTestDataFactory::getOrderProductServiceProductSummaryBasedOn)
                        .map(OrderTestDataFactory::getOrderServiceProductInOrderResponseBasedOn)
                        .toList())
                .build();
    }

    public static OrderCreateRequest getRandomOrderCreateRequest() {
        return Instancio.of(OrderCreateRequest.class).create();
    }

    public static OrderCreateRequest getOrderCreateRequestBasedOnExpectedEntity(
            OrderEntity orderEntityStub
    ) {
        return Instancio.of(OrderCreateRequest.class)
                .set(field("deliveryAddress"), orderEntityStub.getDeliveryAddress())
                .set(field("products"), orderEntityStub.getOrderProducts().stream()
                        .map(it -> OrderProductCreateRequest.builder()
                                .id(it.getId().getProductId())
                                .quantity(it.getOrderedQuantity())
                                .build())
                        .toList())
                .create();
    }

    public static List<OrderProductUpdateRequest> getUpdateRequestList(int length) {
        var list = new ArrayList<OrderProductUpdateRequest>(length);

        for (int i = 0; i < length; i++) {
            list.add(Instancio.of(OrderProductUpdateRequest.class).create());
        }

        return list;
    }

    public static GetOrderResponse getOrderResponse(BaseOrderServiceResponse serviceResponseStub) {
        return serviceResponseStub.products().stream()
                .collect(Collectors.teeing(

                        Collectors.reducing(
                                BigDecimal.ZERO,
                                it -> it.price().multiply(it.quantity()),
                                BigDecimal::add
                        ),

                        Collectors.mapping(
                                orderServiceProductInOrderResponse -> GetOrderProductResponse.builder()
                                        .productId(orderServiceProductInOrderResponse.productId())
                                        .quantity(orderServiceProductInOrderResponse.quantity())
                                        .price(orderServiceProductInOrderResponse.price())
                                        .name(orderServiceProductInOrderResponse.name())
                                        .build(),
                                Collectors.toList()
                        ),

                        (total, convertedProducts) -> GetOrderResponse.builder()
                                .id(serviceResponseStub.orderId())
                                .products(convertedProducts)
                                .totalPrice(total.setScale(2, RoundingMode.HALF_UP))
                                .build()
                ));
    }

    public static OrderServiceCreateCommand getOrderServiceCreateCommand(OrderCreateRequest createRequestStub) {
        return OrderServiceCreateCommand.builder()
                .deliveryAddress(createRequestStub.deliveryAddress())
                .productQuantities(createRequestStub.products().stream().collect(Collectors.toMap(
                        OrderProductCreateRequest::id,
                        OrderProductCreateRequest::quantity,
                        BigDecimal::add
                )))
                .build();
    }

    public static OrderServiceUpdateCommand getOrderServiceUpdateCommand(
            UUID orderId,
            List<OrderProductUpdateRequest> updateRequestStub
    ) {
        return OrderServiceUpdateCommand.builder()
                .orderId(orderId)
                .productQuantities(updateRequestStub.stream().collect(Collectors.toMap(
                        OrderProductUpdateRequest::id,
                        OrderProductUpdateRequest::quantity,
                        BigDecimal::add
                )))
                .build();
    }
}
