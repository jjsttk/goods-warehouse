package com.jjsttk.goodswarehouse.service.order;

import com.jjsttk.goodswarehouse.exception.service.ResourceNotFoundException;
import com.jjsttk.goodswarehouse.exception.service.customer.CustomerBannedException;
import com.jjsttk.goodswarehouse.exception.service.order.NotYourOrderException;
import com.jjsttk.goodswarehouse.exception.service.order.OrderCannotBeCancelledException;
import com.jjsttk.goodswarehouse.exception.service.order.OrderCannotBeUpdatedException;
import com.jjsttk.goodswarehouse.exception.service.product.ReservationException;
import com.jjsttk.goodswarehouse.mapper.order.OrderServiceConverter;
import com.jjsttk.goodswarehouse.mapper.order.product.OrderProductConverter;
import com.jjsttk.goodswarehouse.mapper.product.ProductReservationConverter;
import com.jjsttk.goodswarehouse.persistence.entity.order.OrderEntity;
import com.jjsttk.goodswarehouse.persistence.entity.order.product.OrderProductEntity;
import com.jjsttk.goodswarehouse.persistence.entity.order.product.key.OrderProductId;
import com.jjsttk.goodswarehouse.persistence.repository.OrderRepository;
import com.jjsttk.goodswarehouse.service.customer.CustomerService;
import com.jjsttk.goodswarehouse.service.order.dto.command.CreateOrderCommandInfo;
import com.jjsttk.goodswarehouse.service.order.dto.command.UpdateOrderCommandInfo;
import com.jjsttk.goodswarehouse.service.order.product.OrderProductService;
import com.jjsttk.goodswarehouse.service.order.product.dto.response.OrderProductProjection;
import com.jjsttk.goodswarehouse.service.product.ProductService;
import com.jjsttk.goodswarehouse.service.product.dto.command.ReserveProductCommandInfo;
import com.jjsttk.goodswarehouse.service.product.dto.response.ProductReservationResponse;
import com.jjsttk.goodswarehouse.service.product.dto.response.ReservedProductInfo;
import com.jjsttk.goodswarehouse.shared.enums.order.OrderStatus;
import com.jjsttk.goodswarehouse.testutil.CustomerTestDataFactory;
import com.jjsttk.goodswarehouse.testutil.OrderProductTestDataFactory;
import com.jjsttk.goodswarehouse.testutil.OrderTestDataFactory;
import com.jjsttk.goodswarehouse.testutil.ProductTestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @InjectMocks
    private OrderServiceImpl sut;

    @Mock
    private OrderRepository orderRepositoryMock;

    @Mock
    private OrderServiceConverter orderMapperMock;

    @Mock
    private ProductReservationConverter productReservationMapperMock;

    @Mock
    private OrderProductConverter orderProductMapperMock;

    @Mock
    private ProductService productServiceMock;

    @Mock
    private CustomerService customerServiceMock;

    @Mock
    private OrderProductService orderProductServiceMock;

    @ParameterizedTest
    @MethodSource("provideNotFoundTestCases")
    void getByIdShouldThrowWhenOrderNotFound(
            Long customerId,
            UUID orderId,
            boolean orderExists
    ) {
        // Arrange
        when(orderRepositoryMock.existsByIdAndCustomerId(orderId, customerId))
                .thenReturn(orderExists);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> sut.getById(customerId, orderId));

        // Verify
        verify(orderRepositoryMock).existsByIdAndCustomerId(orderId, customerId);
        verify(orderProductServiceMock, never()).getOrderedProducts(any());
        verify(orderMapperMock, never()).toResponse(any(), any());
    }

    @ParameterizedTest
    @MethodSource("provideFoundTestCases")
    void getByIdShouldReturnResponseWhenOrderExists(
            Long customerId,
            OrderEntity orderEntity,
            OrderStatus expectedStatus
    ) {
        UUID orderId = orderEntity.getId();

        var productsResponse = OrderProductTestDataFactory.getServiceResponseBasedEntity(orderEntity);
        var expectedResponse = OrderTestDataFactory.getBaseOrderServiceResponse(orderEntity);

        when(orderRepositoryMock.existsByIdAndCustomerId(orderId, customerId))
                .thenReturn(true);
        when(orderProductServiceMock.getOrderedProducts(orderId))
                .thenReturn(productsResponse);
        when(orderMapperMock.toResponse(orderId, productsResponse))
                .thenReturn(expectedResponse);

        // Act
        var result = sut.getById(customerId, orderId);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResponse, result);
        assertEquals(expectedStatus, orderEntity.getStatus());

        // Verify
        verify(orderRepositoryMock).existsByIdAndCustomerId(orderId, customerId);
        verify(orderProductServiceMock).getOrderedProducts(orderId);
        verify(orderMapperMock).toResponse(orderId, productsResponse);
    }

    @Test
    void createShouldCreateOrderWhenUserIsActive() {
        var orderEntityWithoutProductsStub =
                OrderTestDataFactory.getOrderEntityWithoutIdAndCustomerIsActive(0, BigDecimal.ONE);
        var customerFromOrderEntityStub =
                orderEntityWithoutProductsStub.getCustomer();
        var customerServiceDtoStub =
                CustomerTestDataFactory.getCustomerServiceDtoFromCustomerEntity(customerFromOrderEntityStub);

        var productToOrderStub = ProductTestDataFactory.getProductEntityWithGeneratedId();

        var orderedQuantityForProduct = productToOrderStub.getQuantity();
        var productQuantitiesEntry =
                Map.entry(productToOrderStub.getId(), orderedQuantityForProduct);
        var createCommand = CreateOrderCommandInfo.builder()
                .deliveryAddress(orderEntityWithoutProductsStub.getDeliveryAddress())
                .productQuantities(
                        Map.ofEntries(productQuantitiesEntry)
                )
                .build();

        var orderProductEntityWithIdStub
                = OrderProductTestDataFactory.getOrderProductEntityWithoutOrderIdRandomProduct(BigDecimal.ONE);

        var productServiceReservationCommandStub =
                ProductTestDataFactory.getReservationCommand(createCommand.productQuantities());

        var productServiceReservationResponseStub =
                ProductTestDataFactory.getReservationResponseWithEmptyProblemsMap(
                        List.of(productToOrderStub),
                        createCommand.productQuantities()
                );

        when(customerServiceMock.getById(customerFromOrderEntityStub.getId()))
                .thenReturn(customerServiceDtoStub);
        when(productReservationMapperMock.toReserveCommand(eq(createCommand.productQuantities())))
                .thenReturn(productServiceReservationCommandStub);
        when(productServiceMock.reserveProductsWithLock(eq(productServiceReservationCommandStub)))
                .thenReturn(productServiceReservationResponseStub);

        var reservedProductInfoStub = ReservedProductInfo.builder()
                .priceAtMoment(productToOrderStub.getPrice())
                .reservedQuantity(orderedQuantityForProduct)
                .build();
        when(orderProductMapperMock.toEntity(productToOrderStub.getId(), reservedProductInfoStub))
                .thenReturn(orderProductEntityWithIdStub);
        when(orderMapperMock.toEntity(customerServiceDtoStub.id(), createCommand.deliveryAddress()))
                .thenReturn(orderEntityWithoutProductsStub);
        when(orderRepositoryMock.saveAndFlush(any(OrderEntity.class)))
                .thenAnswer(invocation -> {
                    OrderEntity entity = invocation.getArgument(0);
                    entity.setId(UUID.randomUUID());

                    return entity;
                });

        var result = sut.create(customerFromOrderEntityStub.getId(), createCommand);

        var listOrderedProductsExpected = List.of(orderProductEntityWithIdStub);

        assertThat(result).isNotNull();
        assertEquals(orderEntityWithoutProductsStub.getOrderProducts(), listOrderedProductsExpected);
        assertNotNull(orderEntityWithoutProductsStub.getOrderProducts().getFirst().getOrder());
        assertNotNull(orderEntityWithoutProductsStub.getOrderProducts().getFirst().getProduct());

        verify(customerServiceMock).getById(anyLong());
        verify(productReservationMapperMock).toReserveCommand(eq(createCommand.productQuantities()));
        verify(productServiceMock).reserveProductsWithLock(eq(productServiceReservationCommandStub));
        verify(orderProductMapperMock).toEntity(any(UUID.class), any(ReservedProductInfo.class));
        verify(orderMapperMock).toEntity(anyLong(), anyString());
    }

    @Test
    void createShouldThrowCustomerBannedException() {
        var createCommand = CreateOrderCommandInfo.builder()
                .deliveryAddress("deliveryAddress")
                .productQuantities(
                        Map.of(UUID.randomUUID(), BigDecimal.ONE)
                )
                .build();

        var bannedCustomer = CustomerTestDataFactory.getCustomerEntityWithGeneratedId(false);
        var bannedCustomerDto = CustomerTestDataFactory.getCustomerServiceDtoFromCustomerEntity(bannedCustomer);

        when(customerServiceMock.getById(bannedCustomer.getId()))
                .thenReturn(bannedCustomerDto);

        var result = assertThrows(CustomerBannedException.class,
                () -> sut.create(bannedCustomer.getId(), createCommand));

        assertThat(result).isInstanceOf(CustomerBannedException.class);
        assertThat(result.getMessage()).containsIgnoringCase("was banned");

        verify(customerServiceMock).getById(bannedCustomer.getId());
        verifyNoInteractions(
                productReservationMapperMock,
                productServiceMock,
                orderProductMapperMock,
                orderMapperMock,
                orderRepositoryMock
        );
    }

    @Test
    void updateShouldUpdateExistingProductsAndOrderDependenciesAndAddNewProduct() {
        // Prepare orderEntity
        var orderProductLengthBeforeUpdate = 2;

        var existingOrder =
                OrderTestDataFactory.getOrderEntityWithIdByLengthAndStatus(
                        orderProductLengthBeforeUpdate,
                        OrderStatus.CREATED,
                        BigDecimal.ONE,
                        true
                );
        var existingOrderId = existingOrder.getId();

        // Prepare existing OrderProductEntity in order
        var existingOrderProduct = existingOrder.getOrderProducts().getFirst();
        assertEquals(existingOrderProduct.getId().getProductId(), existingOrderProduct.getProduct().getId());

        var existingOrderProductPriceOld = BigDecimal.TEN;
        existingOrderProduct.setPrice(existingOrderProductPriceOld);

        var expectedOrderProductPrice = BigDecimal.TWO;
        // Set linked productEntity price equal to expected
        existingOrderProduct.getProduct().setPrice(expectedOrderProductPrice);

        // ProductEntities setup
        var newProductToOrder = ProductTestDataFactory.getProductEntityWithGeneratedId();
        var newProductToOrderQuantityBeforeUpdate = newProductToOrder.getQuantity();
        var valueToAddToExistedProduct = BigDecimal.TEN;
        var valueToReserveNewProduct = BigDecimal.TWO;
        // guarantee we have it in stock for test
        newProductToOrder.setQuantity(valueToReserveNewProduct);
        existingOrderProduct.getProduct().setQuantity(valueToAddToExistedProduct);

        // orderProductMap before update to comparison quantity change right, and change price if updated correctly
        var orderProductIdToSummaryMapBeforeUpdate = new HashMap<UUID, OrderProductProjection>();
        // productMap before update to comparison quantity change right
        var productIdQuantityMapBeforeUpdate = new HashMap<UUID, BigDecimal>();
        existingOrder.getOrderProducts().forEach(it -> {
            orderProductIdToSummaryMapBeforeUpdate.put(
                    it.getId().getProductId(),
                    OrderProductProjection.builder()
                            .productId(it.getId().getProductId())
                            .quantity(it.getOrderedQuantity())
                            .name(it.getProduct().getName())
                            .price(it.getPrice())
                            .build());
            productIdQuantityMapBeforeUpdate.put(
                    it.getProduct().getId(),
                    it.getProduct().getQuantity()
            );
        });

        // updateCommand setup
        // add to 1 existed, and 1 new product
        var existedProductToUpdateEntry =
                Map.entry(existingOrderProduct.getId().getProductId(), valueToAddToExistedProduct);
        var newProductToOrderEntry =
                Map.entry(newProductToOrder.getId(), valueToReserveNewProduct);

        var updateCommand = com.jjsttk.goodswarehouse.service.order.dto.command.UpdateOrderCommandInfo.builder()
                .orderId(existingOrderId)
                .productQuantities(Map.ofEntries(
                        existedProductToUpdateEntry,
                        newProductToOrderEntry
                ))
                .build();

        when(orderRepositoryMock.findByIdForUpdate(existingOrderId))
                .thenReturn(Optional.of(existingOrder));

        var reservationCommandStub = ReserveProductCommandInfo.builder()
                .productQuantities(Map.ofEntries(newProductToOrderEntry))
                .build();

        when(productReservationMapperMock.toReserveCommand(Map.ofEntries(newProductToOrderEntry)))
                .thenReturn(reservationCommandStub);

        var newProductProductInfo = ReservedProductInfo.builder()
                .reservedQuantity(valueToReserveNewProduct)
                .priceAtMoment(newProductToOrder.getPrice())
                .build();
        var reservationResponseStub = ProductReservationResponse.builder()
                .reservedProductsInfoMap(Map.of(
                        newProductToOrder.getId(),
                        newProductProductInfo
                ))
                .problemsMap(Collections.emptyMap())
                .build();
        when(productServiceMock.reserveProductsWithLock(reservationCommandStub))
                .thenReturn(reservationResponseStub);

        var newOrderProductEntityStub = OrderProductEntity.builder()
                .id(new OrderProductId(null, newProductToOrder.getId()))
                .orderedQuantity(newProductProductInfo.reservedQuantity())
                .product(newProductToOrder)
                .price(newProductProductInfo.priceAtMoment())
                .order(null)
                .build();
        when(orderProductMapperMock.toEntity(newProductToOrder.getId(), newProductProductInfo))
                .thenReturn(newOrderProductEntityStub);

        var result = sut.update(existingOrder.getCustomer().getId(), updateCommand);


        // Assert
        assertEquals(existingOrderProduct.getId().getProductId(), existingOrderProduct.getProduct().getId());
        assertNotNull(existingOrderProduct);
        assertNotNull(existingOrderProduct.getProduct());
        assertNotNull(existingOrderProduct.getId());
        assertNotNull(existingOrderProduct.getId().getOrderId());
        assertNotNull(existingOrderProduct.getId().getProductId());

        assertThat(result).isNotNull();
        assertEquals(existingOrder.getId(), result);
        assertThat(orderProductLengthBeforeUpdate).isLessThan(existingOrder.getOrderProducts().size());
        assertThat(existingOrder.getOrderProducts().size()).isEqualTo(3);
        assertThat(existingOrderProduct.getProduct().getId())
                .isEqualTo(existingOrderProduct.getId().getProductId());

        existingOrder.getOrderProducts().forEach(it -> {
            // if it's our new product
            if (it.getId().getProductId().equals(newProductToOrder.getId())) {
                // Check the quantity in stock has decreased
                assertThat(it.getProduct().getQuantity()).isLessThan(newProductToOrderQuantityBeforeUpdate);

                // assert ordered quantity equals to request from updateMap
                // the update map is built based on this value (valueToReserveNewProduct)
                assertThat(it.getOrderedQuantity()).isEqualByComparingTo(valueToReserveNewProduct);
                // check price is equals
                assertEquals(it.getPrice(), it.getProduct().getPrice());

                // assert price is actual
                assertThat(it.getPrice()).isEqualByComparingTo(it.getProduct().getPrice());

                // if updated existed product
            } else if (it.getId().getProductId().equals(existingOrderProduct.getId().getProductId())) {

                // assert current orderProduct quantity is more than in beforeUpdateQuantity
                assertThat(it.getOrderedQuantity()).isGreaterThan(
                        orderProductIdToSummaryMapBeforeUpdate.get(it.getId().getProductId()).quantity()
                );
                // assert current linked productEntity quantity is less than beforeUpdateQuantity
                assertThat(it.getProduct().getQuantity()).isLessThan(
                        productIdQuantityMapBeforeUpdate.get(it.getId().getProductId())
                );
                // assert price updated correctly and actual
                assertThat(it.getPrice()).isEqualByComparingTo(it.getProduct().getPrice());
            }
        });
    }

    @Test
    void updateOrderShouldThrowOrderCannotBeUpdatedExceptionWhenOrderStatusIsNotEqualCreated() {
        var existingOrder =
                OrderTestDataFactory.getOrderEntityWithIdByLengthAndStatus(
                        1,
                        OrderStatus.CONFIRMED,
                        BigDecimal.ONE,
                        true
                );
        var existingOrderId = existingOrder.getId();
        var updateCommandStub = com.jjsttk.goodswarehouse.service.order.dto.command.UpdateOrderCommandInfo.builder()
                .orderId(existingOrderId)
                .productQuantities(Map.of(UUID.randomUUID(), BigDecimal.ONE))
                .build();

        when(orderRepositoryMock.findByIdForUpdate(existingOrderId))
                .thenReturn(Optional.of(existingOrder));

        var exception = assertThrows(OrderCannotBeUpdatedException.class,
                () -> sut.update(existingOrder.getCustomer().getId(), updateCommandStub));

        assertThat(exception.getMessage()).contains("cannot be updated, status is:");

        verify(orderRepositoryMock).findByIdForUpdate(existingOrderId);
        verifyNoMoreInteractions(orderRepositoryMock);
        verifyNoMoreInteractions(productServiceMock);
        verifyNoMoreInteractions(orderProductServiceMock);
    }

    @Test
    void updateOrderStatusShouldUpdateOrderStatus() {
        var orderEntityWithIdStub = OrderTestDataFactory.getOrderEntityWithIdByLengthAndStatus(
                1,
                OrderStatus.CREATED,
                BigDecimal.ONE,
                true
        );

        when(orderRepositoryMock.findById(orderEntityWithIdStub.getId()))
                .thenReturn(Optional.of(orderEntityWithIdStub));

        sut.updateOrderStatus(orderEntityWithIdStub.getId(), OrderStatus.DONE);

        verify(orderRepositoryMock).findById(orderEntityWithIdStub.getId());
        assertThat(orderEntityWithIdStub.getStatus()).isEqualTo(OrderStatus.DONE);
    }

    @Test
    void updateShouldUpdateOnlyExistingProductsAndOrderProductsWhenNoNewProductsInUpdateMap() {
        // Prepare orderEntity
        var orderProductLengthBeforeUpdate = 2;

        var existingOrder =
                OrderTestDataFactory.getOrderEntityWithIdByLengthAndStatus(
                        orderProductLengthBeforeUpdate,
                        OrderStatus.CREATED,
                        BigDecimal.ONE,
                        true
                );
        var existingOrderId = existingOrder.getId();

        // Prepare existing OrderProductEntity in order
        var existingOrderProduct = existingOrder.getOrderProducts().getFirst();
        var existingOrderProductPriceOld = BigDecimal.TEN;
        var expectedOrderProductPriceAfterUpdate = BigDecimal.TWO;
        existingOrderProduct.setPrice(existingOrderProductPriceOld);

        // ProductEntities setup
        var valueToAddToExistedProduct = BigDecimal.TEN;
        // Set linked productEntity price equal to expected
        existingOrderProduct.getProduct().setPrice(expectedOrderProductPriceAfterUpdate);
        // guarantee we have it in stock for test
        existingOrderProduct.getProduct().setQuantity(valueToAddToExistedProduct);

        // orderProductMap before update to comparison quantity change right, and change price if updated correctly
        var orderProductIdToSummaryMapBeforeUpdate = new HashMap<UUID, OrderProductProjection>();
        // productMap before update to comparison quantity change right
        var productIdQuantityMapBeforeUpdate = new HashMap<UUID, BigDecimal>();
        existingOrder.getOrderProducts().forEach(it -> {
            orderProductIdToSummaryMapBeforeUpdate.put(
                    it.getId().getProductId(),
                    OrderProductProjection.builder()
                            .productId(it.getId().getProductId())
                            .quantity(it.getOrderedQuantity())
                            .name(it.getProduct().getName())
                            .price(it.getPrice())
                            .build());
            productIdQuantityMapBeforeUpdate.put(
                    it.getProduct().getId(),
                    it.getProduct().getQuantity()
            );
        });

        // updateCommand setup
        // add to 1 existed
        var existedProductToUpdateEntry =
                Map.entry(existingOrderProduct.getId().getProductId(), valueToAddToExistedProduct);

        var updateCommand = UpdateOrderCommandInfo.builder()
                .orderId(existingOrderId)
                .productQuantities(Map.ofEntries(
                        existedProductToUpdateEntry
                ))
                .build();

        when(orderRepositoryMock.findByIdForUpdate(existingOrderId))
                .thenReturn(Optional.of(existingOrder));

        var result = sut.update(existingOrder.getCustomer().getId(), updateCommand);


        // Assert
        assertEquals(existingOrderProduct.getId().getProductId(), existingOrderProduct.getProduct().getId());
        assertNotNull(existingOrderProduct);
        assertNotNull(existingOrderProduct.getProduct());
        assertNotNull(existingOrderProduct.getId());
        assertNotNull(existingOrderProduct.getId().getOrderId());
        assertNotNull(existingOrderProduct.getId().getProductId());

        assertThat(result).isNotNull();
        assertEquals(existingOrder.getId(), result);
        assertThat(orderProductLengthBeforeUpdate).isEqualTo(existingOrder.getOrderProducts().size());
        assertThat(existingOrderProduct.getProduct().getId())
                .isEqualTo(existingOrderProduct.getId().getProductId());

        existingOrder.getOrderProducts().forEach(it -> {

            if (it.getId().getProductId().equals(existingOrderProduct.getId().getProductId())) {

                // assert current orderProduct quantity is more than in beforeUpdateQuantity
                assertThat(it.getOrderedQuantity()).isGreaterThan(
                        orderProductIdToSummaryMapBeforeUpdate.get(it.getId().getProductId()).quantity()
                );
                // assert current linked productEntity quantity is less than beforeUpdateQuantity
                assertThat(it.getProduct().getQuantity()).isLessThan(
                        productIdQuantityMapBeforeUpdate.get(it.getId().getProductId())
                );
                // assert price updated correctly and actual
                assertThat(it.getPrice()).isEqualByComparingTo(it.getProduct().getPrice());
            }
        });
    }

    @Test
    void updateShouldThrowReservationExceptionWhenProductEntityDoesntHaveQuantityToReserve() {
        // Prepare orderEntity
        var orderProductLengthBeforeUpdate = 2;

        var existingOrder =
                OrderTestDataFactory.getOrderEntityWithIdByLengthAndStatus(
                        orderProductLengthBeforeUpdate,
                        OrderStatus.CREATED,
                        BigDecimal.ONE,
                        true
                );
        var existingOrderId = existingOrder.getId();

        // Prepare existing OrderProductEntity in order
        var existingOrderProduct = existingOrder.getOrderProducts().getFirst();

        // ProductEntities setup
        var valueToAddToExistedProduct = BigDecimal.TEN;
        // guarantee we don`t have it enough in stock for test
        existingOrderProduct.getProduct().setQuantity(valueToAddToExistedProduct.subtract(BigDecimal.ONE));

        // updateCommand setup
        // add to 1 existed
        var existedProductToUpdateEntry =
                Map.entry(existingOrderProduct.getId().getProductId(), valueToAddToExistedProduct);

        var updateCommand = com.jjsttk.goodswarehouse.service.order.dto.command.UpdateOrderCommandInfo.builder()
                .orderId(existingOrderId)
                .productQuantities(Map.ofEntries(
                        existedProductToUpdateEntry
                ))
                .build();

        when(orderRepositoryMock.findByIdForUpdate(existingOrderId))
                .thenReturn(Optional.of(existingOrder));

        var result = assertThrows(ReservationException.class,
                () -> sut.update(existingOrder.getCustomer().getId(), updateCommand));

        // Assert
        assertEquals(existingOrderProduct.getId().getProductId(), existingOrderProduct.getProduct().getId());
        assertNotNull(existingOrderProduct);
        assertNotNull(existingOrderProduct.getProduct());
        assertNotNull(existingOrderProduct.getId());
        assertNotNull(existingOrderProduct.getId().getOrderId());
        assertNotNull(existingOrderProduct.getId().getProductId());

        assertThat(result.getMessage()).contains("Problem: NOT_ENOUGH_QUANTITY");
    }

    @Test
    void updateOrderStatusShouldThrowExceptionWhenOrderIsNotFound() {
        var randomOrderIdStub = UUID.randomUUID();

        when(orderRepositoryMock.findById(randomOrderIdStub))
                .thenReturn(Optional.empty());

        var exc = assertThrows(ResourceNotFoundException.class,
                () -> sut.updateOrderStatus(randomOrderIdStub, OrderStatus.DONE));

        assertThat(exc.getMessage()).contains("not found");

        verify(orderRepositoryMock).findById(randomOrderIdStub);
        verifyNoMoreInteractions(orderRepositoryMock, orderMapperMock);
    }

    @Test
    void cancelOrderWillSetStatusCancelledAndAddOrderedQuantitiesToLinkedProductsWhenCustomerOwnOrder() {
        var existingOrderStub = OrderTestDataFactory.getOrderEntityWithIdByLengthAndStatus(
                1,
                OrderStatus.CREATED,
                BigDecimal.TEN,
                false
        );

        var customerHeaderSecurityStub = existingOrderStub.getCustomer().getId();
        var orderProductEntityStub = existingOrderStub.getOrderProducts().getFirst();
        var productQuantityBeforeReturn = orderProductEntityStub.getProduct().getQuantity();
        var orderedProductQuantity = orderProductEntityStub.getOrderedQuantity();

        when(orderRepositoryMock.findByIdForUpdate(existingOrderStub.getId()))
                .thenReturn(Optional.of(existingOrderStub));

        // Act
        sut.cancel(customerHeaderSecurityStub, existingOrderStub.getId());

        var productQuantityAfterReturn = orderProductEntityStub.getProduct().getQuantity();
        var expectedQuantityAfterReturn = productQuantityBeforeReturn.add(orderedProductQuantity);

        assertThat(productQuantityAfterReturn).isGreaterThan(productQuantityBeforeReturn);
        assertThat(productQuantityAfterReturn).isEqualByComparingTo(expectedQuantityAfterReturn);
        assertThat(existingOrderStub.getStatus()).isEqualByComparingTo(OrderStatus.CANCELLED);
    }

    @Test
    void cancelOrderWillThrowResourceNotFoundExceptionWhenOrderNotFound() {
        var randomUUID = UUID.randomUUID();
        var existingOrderStub = OrderTestDataFactory.getOrderEntityWithIdByLengthAndStatus(
                1,
                OrderStatus.CREATED,
                BigDecimal.TEN,
                false
        );

        var customerHeaderSecurityStub = existingOrderStub.getCustomer().getId();

        when(orderRepositoryMock.findByIdForUpdate(randomUUID))
                .thenReturn(Optional.empty());

        // Act
        var exception = assertThrows(ResourceNotFoundException.class,
                () -> sut.cancel(customerHeaderSecurityStub, randomUUID));

        assertThat(exception.getMessage()).contains("not found");
    }

    @Test
    void cancelOrderWillThrowOrderCannotBeCancelledExceptionWhenOrderStatusIsNotEqualToCreated() {
        var existingOrderStub = OrderTestDataFactory.getOrderEntityWithIdByLengthAndStatus(
                1,
                OrderStatus.CONFIRMED,
                BigDecimal.ONE,
                false
        );
        var customerHeaderSecurityStub = existingOrderStub.getCustomer().getId();

        when(orderRepositoryMock.findByIdForUpdate(existingOrderStub.getId()))
                .thenReturn(Optional.of(existingOrderStub));

        // Act
        var exception = assertThrows(OrderCannotBeCancelledException.class,
                () -> sut.cancel(customerHeaderSecurityStub, existingOrderStub.getId()));

        assertThat(exception.getMessage()).contains("cannot be cancelled, status is: CONFIRMED");
    }

    @Test
    void cancelOrderWillThrowNotYourOrderExceptionWhenCustomerIdHeaderNotEqualToOrderCustomerId() {
        var customerIdSecurityHeader = 1L;

        var existingOrderStub = OrderTestDataFactory.getOrderEntityWithIdByLengthAndStatus(
                1,
                OrderStatus.CREATED,
                BigDecimal.ONE,
                true
        );

        if (customerIdSecurityHeader == existingOrderStub.getCustomer().getId()) {
            existingOrderStub.getCustomer().setId(customerIdSecurityHeader + 1L);
        }

        when(orderRepositoryMock.findByIdForUpdate(existingOrderStub.getId()))
                .thenReturn(Optional.of(existingOrderStub));

        var exception = assertThrows(NotYourOrderException.class,
                () -> sut.cancel(customerIdSecurityHeader, existingOrderStub.getId()));

        assertThat(exception.getMessage()).contains(
                "The customer with id = "
                + customerIdSecurityHeader
                + " is attempting to interact with another customer's order"
        );
    }

    @Test
    void confirmMethodDoNothingBecauseItsStubbed() {
        sut.confirm(1L, UUID.randomUUID());
    }

    private static Stream<Arguments> provideFoundTestCases() {
        return Stream.of(
                Arguments.of(
                        1L,
                        OrderTestDataFactory.getOrderEntityWithIdByLengthAndStatus(
                                1, OrderStatus.CREATED, BigDecimal.ONE, true
                        ),
                        OrderStatus.CREATED
                ),
                Arguments.of(
                        2L,
                        OrderTestDataFactory.getOrderEntityWithIdByLengthAndStatus(
                                2, OrderStatus.CONFIRMED, new BigDecimal("99.99"), false
                        ),
                        OrderStatus.CONFIRMED
                ),
                Arguments.of(
                        3L,
                        OrderTestDataFactory.getOrderEntityWithIdByLengthAndStatus(
                                3, OrderStatus.DONE, new BigDecimal("150.50"), true
                        ),
                        OrderStatus.DONE
                )
        );
    }

    private static Stream<Arguments> provideNotFoundTestCases() {
        return Stream.of(
                // customerId, orderId, orderExists
                Arguments.of(1L, UUID.randomUUID(), false),
                Arguments.of(2L, UUID.randomUUID(), false),
                Arguments.of(999L, UUID.randomUUID(), false)
        );
    }
}
