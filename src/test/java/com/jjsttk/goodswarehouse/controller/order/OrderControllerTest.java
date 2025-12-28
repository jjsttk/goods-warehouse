package com.jjsttk.goodswarehouse.controller.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.exception.InternalServerErrorException;
import com.jjsttk.goodswarehouse.controller.order.dto.request.create.OrderCreateRequest;
import com.jjsttk.goodswarehouse.controller.order.dto.request.create.product.OrderProductCreateRequest;
import com.jjsttk.goodswarehouse.controller.order.dto.request.update.product.OrderProductUpdateRequest;
import com.jjsttk.goodswarehouse.controller.order.dto.request.update.status.OrderUpdateStatusRequest;
import com.jjsttk.goodswarehouse.controller.order.dto.response.GetOrderResponse;
import com.jjsttk.goodswarehouse.exception.handler.GlobalExceptionHandler;
import com.jjsttk.goodswarehouse.exception.service.ResourceNotFoundException;
import com.jjsttk.goodswarehouse.exception.service.customer.CustomerBannedException;
import com.jjsttk.goodswarehouse.exception.service.order.NotEnoughQuantityInStockException;
import com.jjsttk.goodswarehouse.exception.service.order.NotYourOrderException;
import com.jjsttk.goodswarehouse.exception.service.order.OrderCannotBeCancelledException;
import com.jjsttk.goodswarehouse.exception.service.order.OrderCannotBeUpdatedException;
import com.jjsttk.goodswarehouse.exception.service.order.product.ProductsToOrderNotFoundException;
import com.jjsttk.goodswarehouse.mapper.order.OrderControllerConverter;
import com.jjsttk.goodswarehouse.persistence.entity.order.OrderEntity;
import com.jjsttk.goodswarehouse.persistence.entity.product.ProductEntity;
import com.jjsttk.goodswarehouse.service.order.OrderService;
import com.jjsttk.goodswarehouse.shared.enums.order.OrderStatus;
import com.jjsttk.goodswarehouse.testutil.OrderTestDataFactory;
import com.jjsttk.goodswarehouse.testutil.StringTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private OrderService orderServiceMock;

    @Mock
    private OrderControllerConverter mapperMock;

    @InjectMocks
    private OrderControllerImpl sut;

    private static final long CUSTOMER_ID_HEADER = 1L;
    private static final UUID ORDER_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(sut)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
    }

    // getOrderById 200
    @Test
    void getOrderByIdShouldReturnResponseOrderWhenOrderExistsAndCustomerHeaderEqualToCustomerFromOrder()
            throws Exception {

        var orderEntity =
                OrderTestDataFactory.getOrderEntityWithIdByLengthAndStatus(
                        5,
                        OrderStatus.CONFIRMED,
                        BigDecimal.TEN,
                        true
                );
        var serviceResponseStub =
                OrderTestDataFactory.getBaseOrderServiceResponse(orderEntity);
        var sutResponseStub = OrderTestDataFactory.getOrderResponse(serviceResponseStub);

        when(orderServiceMock.getById(CUSTOMER_ID_HEADER, ORDER_ID))
                .thenReturn(serviceResponseStub);
        when(mapperMock.toResponse(serviceResponseStub))
                .thenReturn(sutResponseStub);

        var responseJson = mockMvc.perform(get("/api/v1/orders/{id}", ORDER_ID)
                        .header("customerId", CUSTOMER_ID_HEADER))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var actualResponse = objectMapper.readValue(
                responseJson, GetOrderResponse.class);

        assertEquals(sutResponseStub.id(), actualResponse.id());
        assertEquals(sutResponseStub.products().size(), actualResponse.products().size());
        assertEquals(sutResponseStub.totalPrice(), actualResponse.totalPrice());

        for (int i = 0; i < sutResponseStub.products().size(); i++) {
            var expectedProduct = sutResponseStub.products().get(i);
            var actualProduct = actualResponse.products().get(i);

            assertEquals(expectedProduct.productId(), actualProduct.productId());
            assertEquals(expectedProduct.name(), actualProduct.name());
            assertEquals(expectedProduct.quantity(), actualProduct.quantity());
            assertEquals(expectedProduct.price(), actualProduct.price());
        }

        verify(orderServiceMock, times(1)).getById(CUSTOMER_ID_HEADER, ORDER_ID);
        verify(mapperMock, times(1)).toResponse(serviceResponseStub);
    }

    // getOrderById 404 Order not found
    @Test
    void getOrderByIdShouldThrowResourceNotFoundExceptionWhenOrderDoesNotExist() throws Exception {
        var notExistOrderId = UUID.randomUUID();

        when(orderServiceMock.getById(CUSTOMER_ID_HEADER, notExistOrderId))
                .thenThrow(new ResourceNotFoundException(OrderEntity.class, notExistOrderId));

        mockMvc.perform(get("/api/v1/orders/{id}", notExistOrderId)
                        .header("customerId", CUSTOMER_ID_HEADER))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(
                        String.format("Resource OrderEntity with id = %s not found", notExistOrderId))
                )
                .andExpect(jsonPath("$.exception").value("ResourceNotFoundException"))
                .andExpect(jsonPath("$.source").isNotEmpty())
                .andExpect(jsonPath("$.dateTime").isNotEmpty());

        verify(orderServiceMock, times(1)).getById(CUSTOMER_ID_HEADER, notExistOrderId);
        verifyNoInteractions(mapperMock);
    }

    // getOrderById 403 customerId not owns order
    @Test
    void getOrderByIdShouldThrowNotYourOrderExceptionWhenCustomerTriesToGetAnotherCustomersOrder() throws Exception {

        when(orderServiceMock.getById(CUSTOMER_ID_HEADER, ORDER_ID))
                .thenThrow(new NotYourOrderException(CUSTOMER_ID_HEADER));

        mockMvc.perform(get("/api/v1/orders/{id}", ORDER_ID)
                        .header("customerId", CUSTOMER_ID_HEADER))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(
                        "The customer with id = "
                        + CUSTOMER_ID_HEADER
                        + " is attempting to interact with another customer's order"
                ))
                .andExpect(jsonPath("$.exception").value("NotYourOrderException"))
                .andExpect(jsonPath("$.source").isNotEmpty())
                .andExpect(jsonPath("$.dateTime").isNotEmpty());

        verify(orderServiceMock, times(1)).getById(CUSTOMER_ID_HEADER, ORDER_ID);
        verifyNoInteractions(mapperMock);
    }

    // getById 500
    @Test
    void getOrderByIdShouldThrowInternalServerErrorExceptionWhenSmthWrong() throws Exception {
        when(orderServiceMock.getById(CUSTOMER_ID_HEADER, ORDER_ID))
                .thenThrow(new InternalServerErrorException("Internal Server Error"));

        mockMvc.perform(get("/api/v1/orders/{id}", ORDER_ID)
                        .header("customerId", CUSTOMER_ID_HEADER))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value(
                        "Status 500: Internal Server Error"))
                .andExpect(jsonPath("$.exception").value("InternalServerErrorException"))
                .andExpect(jsonPath("$.source").isNotEmpty())
                .andExpect(jsonPath("$.dateTime").isNotEmpty());

        verifyNoInteractions(mapperMock);
    }

    // createOrder 201
    @Test
    void createShouldCreateOrderAndReturnId() throws Exception {
        var orderEntityStub =
                OrderTestDataFactory.getOrderEntityWithIdByLengthAndStatus(
                        5,
                        OrderStatus.CREATED,
                        BigDecimal.ONE,
                        true
                );
        var customerIdHeader = orderEntityStub.getCustomer().getId();
        var createRequestStub =
                OrderTestDataFactory.getOrderCreateRequestBasedOnExpectedEntity(orderEntityStub);
        var serviceCommandStub = OrderTestDataFactory.getOrderServiceCreateCommand(createRequestStub);


        when(mapperMock.toServiceCommand(createRequestStub))
                .thenReturn(serviceCommandStub);
        when(orderServiceMock.create(customerIdHeader, serviceCommandStub))
                .thenReturn(orderEntityStub.getId());

        var result = mockMvc.perform(post("/api/v1/orders")
                        .header("customerId", customerIdHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequestStub)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var uuidResult = objectMapper.readValue(result, UUID.class);
        assertThat(uuidResult).isEqualByComparingTo(orderEntityStub.getId());
        assertThat(orderEntityStub.getOrderProducts()).isNotEmpty();

        verify(mapperMock, times(1)).toServiceCommand(createRequestStub);
        verify(orderServiceMock, times(1)).create(customerIdHeader, serviceCommandStub);
    }

    // createOrder 400 MethodArgumentNotValidException
    @Test
    void createShouldThrowValidationExceptionWhenIncRequestDataIsNotValid() throws Exception {
        // (deliveryAddress.length() > 150) && (quantity.negate())
        var nonValidCreateRequestStub = OrderCreateRequest.builder()
                .deliveryAddress(StringTestUtils.getStringByLength(151))
                .products(List.of(
                        new OrderProductCreateRequest(UUID.randomUUID(), BigDecimal.ONE.negate()),
                        new OrderProductCreateRequest(UUID.randomUUID(), BigDecimal.TEN.negate())
                ))
                .build();

        var result = mockMvc.perform(post("/api/v1/orders")
                        .header("customerId", CUSTOMER_ID_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nonValidCreateRequestStub)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.exception").value("MethodArgumentNotValidException"))
                .andExpect(jsonPath("$.source").isNotEmpty())
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).containsIgnoringCase(
                "products[0].quantity: Quantity for ordering product must be positive"
        );
        assertThat(result).containsIgnoringCase(
                "products[1].quantity: Quantity for ordering product must be positive"
        );
        assertThat(result).containsIgnoringCase(
                "deliveryAddress: Delivery address must be no longer than 150 characters"
        );

        verifyNoInteractions(mapperMock);
        verifyNoInteractions(orderServiceMock);
    }

    // createOrder 403 customer banned
    @Test
    void createShouldThrowCustomerBannedExceptionWhenCustomerIsActiveFalse() throws Exception {
        var orderCreateRequestStub =
                OrderTestDataFactory.getRandomOrderCreateRequest();
        var createCommandStub =
                OrderTestDataFactory.getOrderServiceCreateCommand(orderCreateRequestStub);

        when(mapperMock.toServiceCommand(orderCreateRequestStub))
                .thenReturn(createCommandStub);
        when(orderServiceMock.create(CUSTOMER_ID_HEADER, createCommandStub))
                .thenThrow(new CustomerBannedException(CUSTOMER_ID_HEADER));

        var result = mockMvc.perform(post("/api/v1/orders")
                        .header("customerId", CUSTOMER_ID_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderCreateRequestStub)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.exception").value("CustomerBannedException"))
                .andExpect(jsonPath("$.source").isNotEmpty())
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).containsIgnoringCase(
                "Customer with id = " + CUSTOMER_ID_HEADER + " was banned"
        );

        verify(mapperMock).toServiceCommand(orderCreateRequestStub);
        verify(orderServiceMock).create(CUSTOMER_ID_HEADER, createCommandStub);
        verifyNoMoreInteractions(mapperMock);
        verifyNoMoreInteractions(orderServiceMock);
    }

    // createOrder 404 product to order not found
    @Test
    void createShouldThrowResourceNotFoundExceptionWithProductIdWhenProductToOrderNotFound() throws Exception {
        var createRequestStub = OrderTestDataFactory.getRandomOrderCreateRequest();
        var createCommandStub = OrderTestDataFactory.getOrderServiceCreateCommand(createRequestStub);
        var badProductId = createRequestStub.products().getFirst().id();

        when(mapperMock.toServiceCommand(createRequestStub))
                .thenReturn(createCommandStub);
        when(orderServiceMock.create(CUSTOMER_ID_HEADER, createCommandStub))
                .thenThrow(new ResourceNotFoundException(
                        ProductEntity.class,
                        badProductId
                ));

        var result = mockMvc.perform(post("/api/v1/orders")
                        .header("customerId", CUSTOMER_ID_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequestStub)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.exception").value("ResourceNotFoundException"))
                .andExpect(jsonPath("$.source").isNotEmpty())
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).containsIgnoringCase(
                "ProductEntity with id = " + badProductId + " not found"
        );

        verify(mapperMock).toServiceCommand(createRequestStub);
        verify(orderServiceMock).create(CUSTOMER_ID_HEADER, createCommandStub);
        verifyNoMoreInteractions(mapperMock);
        verifyNoMoreInteractions(orderServiceMock);
    }

    // createOrder 409 when not enough product quantity in stock now
    @Test
    void createShouldThrowNotEnoughQuantityExceptionWhenProductQuantityIsInsufficient() throws Exception {
        var createRequestStub = OrderTestDataFactory.getRandomOrderCreateRequest();
        var createCommandStub = OrderTestDataFactory.getOrderServiceCreateCommand(createRequestStub);
        var badProductId = createRequestStub.products().getFirst().id();

        when(mapperMock.toServiceCommand(createRequestStub))
                .thenReturn(createCommandStub);
        when(orderServiceMock.create(CUSTOMER_ID_HEADER, createCommandStub))
                .thenThrow(new NotEnoughQuantityInStockException(badProductId));

        var result = mockMvc.perform(post("/api/v1/orders")
                        .header("customerId", CUSTOMER_ID_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequestStub)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.exception").value("NotEnoughQuantityInStockException"))
                .andExpect(jsonPath("$.source").isNotEmpty())
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).containsIgnoringCase(
                "Not enough quantity in stock for product productId = " + badProductId
        );

        verify(mapperMock).toServiceCommand(createRequestStub);
        verify(orderServiceMock).create(CUSTOMER_ID_HEADER, createCommandStub);
        verifyNoMoreInteractions(mapperMock);
        verifyNoMoreInteractions(orderServiceMock);
    }

    // updateOrder 200
    @Test
    void updateOrderShouldUpdateOrderWhenRequestIsValidAndCustomerOwnsOrderAndIsActiveTrue() throws Exception {
        var updateRequestStub = OrderTestDataFactory.getUpdateRequestList(2);

        var updateCommandStub =
                OrderTestDataFactory.getOrderServiceUpdateCommand(ORDER_ID, updateRequestStub);


        when(mapperMock.toServiceCommand(ORDER_ID, updateRequestStub))
                .thenReturn(updateCommandStub);
        when(orderServiceMock.update(CUSTOMER_ID_HEADER, updateCommandStub))
                .thenReturn(ORDER_ID);

        var result = mockMvc.perform(patch("/api/v1/orders/{orderId}", ORDER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestStub))
                        .header("customerId", CUSTOMER_ID_HEADER))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var uuidResult = objectMapper.readValue(result, UUID.class);

        assertEquals(uuidResult, ORDER_ID);
        verify(mapperMock).toServiceCommand(ORDER_ID, updateRequestStub);
        verify(orderServiceMock).update(CUSTOMER_ID_HEADER, updateCommandStub);
        verifyNoMoreInteractions(mapperMock);
        verifyNoMoreInteractions(orderServiceMock);
    }

    // updateOrder 400 HandlerMethodValidationException
    @Test
    void updateOrderShouldThrowValidationErrorWhenUpdateDataNotValid() throws Exception {
        var nonValidUpdateRequestListStub =
                List.of(
                        OrderProductUpdateRequest.builder()
                                .id(UUID.randomUUID())
                                .quantity(null) // quantity null
                                .build(),
                        OrderProductUpdateRequest.builder()
                                .id(UUID.randomUUID())
                                .quantity(BigDecimal.TEN.negate()) // negate
                                .build()
                );

        var result = mockMvc.perform(patch("/api/v1/orders/{orderId}", ORDER_ID)
                        .header("customerId", CUSTOMER_ID_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nonValidUpdateRequestListStub)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.exception").value("HandlerMethodValidationException"))
                .andExpect(jsonPath("$.source").isNotEmpty())
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).containsIgnoringCase(
                "must not be null"
        );
        assertThat(result).containsIgnoringCase(
                "Quantity for ordering product must be positive"
        );

        verifyNoInteractions(mapperMock);
        verifyNoInteractions(orderServiceMock);
    }

    // updateOrder 403 not customerId`s order to update
    @Test
    void updateOrderShouldThrowNotYourOrderExceptionWhenCustomerIdNotEqualToOrderCustomerId() throws Exception {
        var updateRequestStub =
                OrderTestDataFactory.getUpdateRequestList(2);
        var updateCommandStub =
                OrderTestDataFactory.getOrderServiceUpdateCommand(ORDER_ID, updateRequestStub);

        when(mapperMock.toServiceCommand(ORDER_ID, updateRequestStub))
                .thenReturn(updateCommandStub);
        when(orderServiceMock.update(CUSTOMER_ID_HEADER, updateCommandStub))
                .thenThrow(new NotYourOrderException(CUSTOMER_ID_HEADER));

        var result = mockMvc.perform(patch("/api/v1/orders/{orderId}", ORDER_ID)
                        .header("customerId", CUSTOMER_ID_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestStub)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.exception").value("NotYourOrderException"))
                .andExpect(jsonPath("$.source").isNotEmpty())
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).containsIgnoringCase(
                "The customer with id = "
                + CUSTOMER_ID_HEADER
                + " is attempting to interact with another customer's order"
        );

        verify(mapperMock).toServiceCommand(ORDER_ID, updateRequestStub);
        verify(orderServiceMock).update(CUSTOMER_ID_HEADER, updateCommandStub);
        verifyNoMoreInteractions(mapperMock);
        verifyNoMoreInteractions(orderServiceMock);
    }

    // updateOrder 404 when product not found
    @Test
    void updateOrderShouldThrowProductsToOrderNotFoundWhenProductIdsNotFound() throws Exception {
        var updateRequestStub =
                OrderTestDataFactory.getUpdateRequestList(2);
        var updateCommandStub =
                OrderTestDataFactory.getOrderServiceUpdateCommand(ORDER_ID, updateRequestStub);
        var badProductId = updateRequestStub.getFirst().id();

        when(mapperMock.toServiceCommand(ORDER_ID, updateRequestStub))
                .thenReturn(updateCommandStub);
        when(orderServiceMock.update(CUSTOMER_ID_HEADER, updateCommandStub))
                .thenThrow(new ProductsToOrderNotFoundException(List.of(badProductId)));

        var result = mockMvc.perform(patch("/api/v1/orders/{orderId}", ORDER_ID)
                        .header("customerId", CUSTOMER_ID_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestStub)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.exception").value("ProductsToOrderNotFoundException"))
                .andExpect(jsonPath("$.source").isNotEmpty())
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).containsIgnoringCase(
                "Products not found with id`s: " + List.of(badProductId)
        );

        verify(mapperMock).toServiceCommand(ORDER_ID, updateRequestStub);
        verify(orderServiceMock).update(CUSTOMER_ID_HEADER, updateCommandStub);
        verifyNoMoreInteractions(mapperMock);
        verifyNoMoreInteractions(orderServiceMock);
    }

    // updateOrder 404 when order not found
    @Test
    void updateOrderShouldThrowResourceNotFoundOrderNotFound() throws Exception {
        var updateRequestStub =
                OrderTestDataFactory.getUpdateRequestList(2);
        var updateCommandStub =
                OrderTestDataFactory.getOrderServiceUpdateCommand(ORDER_ID, updateRequestStub);

        when(mapperMock.toServiceCommand(ORDER_ID, updateRequestStub))
                .thenReturn(updateCommandStub);
        when(orderServiceMock.update(CUSTOMER_ID_HEADER, updateCommandStub))
                .thenThrow(new ResourceNotFoundException(OrderEntity.class, ORDER_ID));

        var result = mockMvc.perform(patch("/api/v1/orders/{orderId}", ORDER_ID)
                        .header("customerId", CUSTOMER_ID_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestStub)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.exception").value("ResourceNotFoundException"))
                .andExpect(jsonPath("$.source").isNotEmpty())
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).containsIgnoringCase(
                "OrderEntity with id = " + ORDER_ID + " not found"
        );

        verify(mapperMock).toServiceCommand(ORDER_ID, updateRequestStub);
        verify(orderServiceMock).update(CUSTOMER_ID_HEADER, updateCommandStub);
        verifyNoMoreInteractions(mapperMock);
        verifyNoMoreInteractions(orderServiceMock);
    }

    // updateOrder 409 orderStatus != OrderStatus.CREATED
    @Test
    void updateOrderShouldThrowOrderCannotBeUpdatedExceptionWhenOrderInWrongStatus() throws Exception {
        var updateRequestStub =
                OrderTestDataFactory.getUpdateRequestList(2);
        var updateCommandStub =
                OrderTestDataFactory.getOrderServiceUpdateCommand(ORDER_ID, updateRequestStub);


        when(mapperMock.toServiceCommand(ORDER_ID, updateRequestStub))
                .thenReturn(updateCommandStub);
        when(orderServiceMock.update(CUSTOMER_ID_HEADER, updateCommandStub))
                .thenThrow(new OrderCannotBeUpdatedException(ORDER_ID, OrderStatus.CONFIRMED));

        var result = mockMvc.perform(patch("/api/v1/orders/{orderId}", ORDER_ID)
                        .header("customerId", CUSTOMER_ID_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestStub)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.exception").value("OrderCannotBeUpdatedException"))
                .andExpect(jsonPath("$.source").isNotEmpty())
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).containsIgnoringCase(
                "Order with id: " + ORDER_ID + " cannot be updated, status is: " + OrderStatus.CONFIRMED
        );

        verify(mapperMock).toServiceCommand(ORDER_ID, updateRequestStub);
        verify(orderServiceMock).update(CUSTOMER_ID_HEADER, updateCommandStub);
        verifyNoMoreInteractions(mapperMock);
        verifyNoMoreInteractions(orderServiceMock);
    }

    // cancelOrder 204 soft
    @Test
    void cancelOrderShouldCancelOrderWhenOrderInRightStatusAndCustomerOwnsOrder() throws Exception {
        doNothing().when(orderServiceMock).cancel(CUSTOMER_ID_HEADER, ORDER_ID);

        mockMvc.perform(delete("/api/v1/orders/{orderId}", ORDER_ID)
                        .header("customerId", CUSTOMER_ID_HEADER))
                .andExpect(status().isNoContent());

        verify(orderServiceMock).cancel(CUSTOMER_ID_HEADER, ORDER_ID);
        verifyNoInteractions(mapperMock);
        verifyNoMoreInteractions(orderServiceMock);
    }

    // cancelOrder 403 customerId not equal to Order.customer.id
    @Test
    void cancelOrderShouldThrowUpdateNotYourOrderExceptionWhenWrongCustomerId() throws Exception {
        doThrow(new NotYourOrderException(CUSTOMER_ID_HEADER))
                .when(orderServiceMock).cancel(CUSTOMER_ID_HEADER, ORDER_ID);

        var res = mockMvc.perform(delete("/api/v1/orders/{orderId}", ORDER_ID)
                        .header("customerId", CUSTOMER_ID_HEADER))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.exception").value("NotYourOrderException"))
                .andExpect(jsonPath("$.source").isNotEmpty())
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(res).containsIgnoringCase(
                "The customer with id = "
                + CUSTOMER_ID_HEADER
                + " is attempting to interact with another customer's order"
        );

        verify(orderServiceMock).cancel(CUSTOMER_ID_HEADER, ORDER_ID);
        verifyNoInteractions(mapperMock);
        verifyNoMoreInteractions(orderServiceMock);
    }

    // cancelOrder 404 order not found
    @Test
    void cancelOrderShouldThrowResourceNotFoundExceptionWhenOrderNotFound() throws Exception {
        doThrow(new ResourceNotFoundException(OrderEntity.class, ORDER_ID))
                .when(orderServiceMock).cancel(CUSTOMER_ID_HEADER, ORDER_ID);

        var res = mockMvc.perform(delete("/api/v1/orders/{orderId}", ORDER_ID)
                        .header("customerId", CUSTOMER_ID_HEADER))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.exception").value("ResourceNotFoundException"))
                .andExpect(jsonPath("$.source").isNotEmpty())
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(res).containsIgnoringCase(
                "OrderEntity with id = "
                + ORDER_ID
                + " not found"
        );

        verify(orderServiceMock).cancel(CUSTOMER_ID_HEADER, ORDER_ID);
        verifyNoInteractions(mapperMock);
        verifyNoMoreInteractions(orderServiceMock);
    }

    // cancelOrder 409 OrderStatus not equal to CREATED
    @Test
    void cancelOrderShouldThrowOrderCannotBeCancelledExceptionWhenWrongStatus() throws Exception {
        doThrow(new OrderCannotBeCancelledException(ORDER_ID, OrderStatus.CONFIRMED))
                .when(orderServiceMock).cancel(CUSTOMER_ID_HEADER, ORDER_ID);

        var res = mockMvc.perform(delete("/api/v1/orders/{orderId}", ORDER_ID)
                        .header("customerId", CUSTOMER_ID_HEADER))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.exception").value("OrderCannotBeCancelledException"))
                .andExpect(jsonPath("$.source").isNotEmpty())
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(res).containsIgnoringCase(
                "Order with id: "
                + ORDER_ID
                + " cannot be cancelled, status is: "
                + OrderStatus.CONFIRMED
        );

        verify(orderServiceMock).cancel(CUSTOMER_ID_HEADER, ORDER_ID);
        verifyNoInteractions(mapperMock);
        verifyNoMoreInteractions(orderServiceMock);
    }

    // stubbed in service
    @Test
    void confirmOrderShouldSetConfirmStatus() throws Exception {
        doNothing().when(orderServiceMock).confirm(CUSTOMER_ID_HEADER, ORDER_ID);

        mockMvc.perform(post("/api/v1/orders/{orderId}/confirm", ORDER_ID)
                        .header("customerId", CUSTOMER_ID_HEADER))
                .andExpect(status().isOk());
    }


    // updateOrderStatus 200
    @Test
    void updateOrderStatusShouldSetStatus() throws Exception {
        var updateStatusRequestStub = OrderUpdateStatusRequest.builder()
                .status(OrderStatus.DONE)
                .build();

        doNothing().when(orderServiceMock).updateOrderStatus(
                CUSTOMER_ID_HEADER,
                ORDER_ID,
                updateStatusRequestStub.status()
        );

        mockMvc.perform(patch("/api/v1/orders/{orderId}/status", ORDER_ID)
                        .header("customerId", CUSTOMER_ID_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateStatusRequestStub)))
                .andExpect(status().isOk());

        verify(orderServiceMock).updateOrderStatus(
                CUSTOMER_ID_HEADER,
                ORDER_ID,
                updateStatusRequestStub.status());

        verifyNoInteractions(mapperMock);
        verifyNoMoreInteractions(orderServiceMock);
    }

    // updateOrderStatus 404 when order not found
    @Test
    void updateOrderStatusShouldThrowResourceNotFoundExceptionWhenOrderNotFound() throws Exception {
        var orderUpdateStatusRequestStub = OrderUpdateStatusRequest.builder()
                .status(OrderStatus.CONFIRMED)
                .build();

        doThrow(new ResourceNotFoundException(OrderEntity.class, ORDER_ID)).when(orderServiceMock).updateOrderStatus(
                CUSTOMER_ID_HEADER,
                ORDER_ID,
                orderUpdateStatusRequestStub.status()
        );

        mockMvc.perform(patch("/api/v1/orders/{orderId}/status", ORDER_ID)
                        .header("customerId", CUSTOMER_ID_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderUpdateStatusRequestStub)))
                .andExpect(status().isNotFound());

        verify(orderServiceMock).updateOrderStatus(
                CUSTOMER_ID_HEADER,
                ORDER_ID,
                orderUpdateStatusRequestStub.status());

        verifyNoInteractions(mapperMock);
        verifyNoMoreInteractions(orderServiceMock);
    }

    // updateOrderStatus 403 forbidden when customerId header not equal to order.customer.id
    @Test
    void updateOrderStatusShouldThrowNotYourOrderExceptionWhenCustomerNotOwnOrder() throws Exception {
        var updateStatusRequestStub = OrderUpdateStatusRequest.builder()
                .status(OrderStatus.CONFIRMED)
                .build();

        doThrow(new NotYourOrderException(CUSTOMER_ID_HEADER)).when(orderServiceMock).updateOrderStatus(
                CUSTOMER_ID_HEADER,
                ORDER_ID,
                updateStatusRequestStub.status()
        );

        mockMvc.perform(patch("/api/v1/orders/{orderId}/status", ORDER_ID)
                        .header("customerId", CUSTOMER_ID_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateStatusRequestStub)))
                .andExpect(status().isForbidden());

        verify(orderServiceMock).updateOrderStatus(
                CUSTOMER_ID_HEADER,
                ORDER_ID,
                updateStatusRequestStub.status());

        verifyNoInteractions(mapperMock);
        verifyNoMoreInteractions(orderServiceMock);
    }
}
