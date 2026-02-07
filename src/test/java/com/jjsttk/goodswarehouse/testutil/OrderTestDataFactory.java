package com.jjsttk.goodswarehouse.testutil;

import com.jjsttk.goodswarehouse.controller.order.dto.request.create.OrderCreateRequest;
import com.jjsttk.goodswarehouse.controller.order.dto.request.create.product.OrderProductCreateRequest;
import com.jjsttk.goodswarehouse.controller.order.dto.request.update.product.OrderProductUpdateRequest;
import com.jjsttk.goodswarehouse.controller.order.dto.response.GetOrderResponse;
import com.jjsttk.goodswarehouse.controller.order.dto.response.product.GetOrderProductResponse;
import com.jjsttk.goodswarehouse.persistence.entity.order.OrderEntity;
import com.jjsttk.goodswarehouse.persistence.entity.order.product.OrderProductEntity;
import com.jjsttk.goodswarehouse.service.order.dto.command.CreateOrderCommandInfo;
import com.jjsttk.goodswarehouse.service.order.dto.command.UpdateOrderCommandInfo;
import com.jjsttk.goodswarehouse.service.order.dto.response.BaseOrderResponse;
import com.jjsttk.goodswarehouse.service.order.dto.response.BaseProductInOrderResponse;
import com.jjsttk.goodswarehouse.persistence.repository.projections.OrderProductSummaryProjection;
import com.jjsttk.goodswarehouse.shared.enums.exchange.PriceCurrency;
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
        var orderProducts = new ArrayList<OrderProductEntity>(products.size());
        for (var product : products) {
            var op = OrderProductTestDataFactory.getOrderProductEntityWithoutOrderIdProductBased(
                    product,
                    orderedQuantityForAll
            );

            op.getId().setOrderId(orderId);

            orderProducts.add(op);
        }

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

    public static BaseProductInOrderResponse getBaseProductInOrderResponseBasedOn(
            OrderProductSummaryProjection orderProductSummary
    ) {
        return Instancio.of(BaseProductInOrderResponse.class)
                .set(field("productId"), orderProductSummary.productId())
                .set(field("name"), orderProductSummary.name())
                .set(field("quantity"), orderProductSummary.quantity())
                .set(field("price"), orderProductSummary.price())
                .create();
    }

    public static BaseOrderResponse getBaseOrderResponse(OrderEntity orderEntity) {
        return BaseOrderResponse.builder()
                .orderId(orderEntity.getId())
                .products(orderEntity.getOrderProducts().stream()
                        .map(OrderProductTestDataFactory::getOrderProductProjectionBasedOn)
                        .map(OrderTestDataFactory::getBaseProductInOrderResponseBasedOn)
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

    public static List<OrderProductUpdateRequest> getOrderProductUpdateRequestList(int length) {
        return Instancio.ofList(OrderProductUpdateRequest.class)
                .size(length)
                .create();
    }

    public static GetOrderResponse getGetOrderResponseRubCurrency(BaseOrderResponse serviceResponseStub) {
        return serviceResponseStub.products().stream().collect(Collectors.teeing(

                Collectors.mapping(it -> GetOrderProductResponse.builder()
                                .productId(it.productId())
                                .name(it.name())
                                .price(it.price())
                                .quantity(it.quantity())
                                .build(),
                        Collectors.toList()),

                Collectors.reducing(
                        BigDecimal.ZERO,
                        it -> it.price().multiply(it.quantity()),
                        BigDecimal::add
                ),

                (convertedProducts, totalPrice) -> GetOrderResponse.builder()
                        .id(serviceResponseStub.orderId())
                        .currency(PriceCurrency.RUB)
                        .products(convertedProducts)
                        .totalPrice(totalPrice.setScale(2, RoundingMode.HALF_UP))
                        .build()
        ));
    }

    public static CreateOrderCommandInfo getCreateOrderCommandInfo(OrderCreateRequest createRequestStub) {
        return CreateOrderCommandInfo.builder()
                .deliveryAddress(createRequestStub.deliveryAddress())
                .productQuantities(createRequestStub.products().stream().collect(Collectors.toMap(
                        OrderProductCreateRequest::id,
                        OrderProductCreateRequest::quantity,
                        BigDecimal::add
                )))
                .build();
    }

    public static UpdateOrderCommandInfo getUpdateOrderCommandInfo(
            UUID orderId,
            List<OrderProductUpdateRequest> updateRequestStub
    ) {
        return UpdateOrderCommandInfo.builder()
                .orderId(orderId)
                .productQuantities(updateRequestStub.stream().collect(Collectors.toMap(
                        OrderProductUpdateRequest::id,
                        OrderProductUpdateRequest::quantity,
                        BigDecimal::add
                )))
                .build();
    }
}
