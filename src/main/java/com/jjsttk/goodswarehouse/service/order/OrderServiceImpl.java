package com.jjsttk.goodswarehouse.service.order;

import com.jjsttk.goodswarehouse.controller.order.dto.response.OrderInfo;
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
import com.jjsttk.goodswarehouse.persistence.entity.product.ProductEntity;
import com.jjsttk.goodswarehouse.persistence.repository.OrderRepository;
import com.jjsttk.goodswarehouse.service.customer.CustomerService;
import com.jjsttk.goodswarehouse.service.customer.dto.response.BaseCustomerInfoDto;
import com.jjsttk.goodswarehouse.service.order.dto.command.CreateOrderCommandInfo;
import com.jjsttk.goodswarehouse.service.order.dto.command.UpdateOrderCommandInfo;
import com.jjsttk.goodswarehouse.service.order.dto.internal.CustomerExternalData;
import com.jjsttk.goodswarehouse.service.order.dto.internal.DetailedOrderContext;
import com.jjsttk.goodswarehouse.service.order.dto.response.BaseOrderResponse;
import com.jjsttk.goodswarehouse.service.order.product.OrderProductService;
import com.jjsttk.goodswarehouse.service.product.ProductService;
import com.jjsttk.goodswarehouse.service.product.dto.response.ProductReservationResponse;
import com.jjsttk.goodswarehouse.shared.enums.order.OrderStatus;
import com.jjsttk.goodswarehouse.shared.enums.product.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderServiceConverter orderMapper;
    private final ProductReservationConverter productReservationMapper;
    private final OrderProductConverter orderProductMapper;

    private final ProductService productService;
    private final CustomerService customerService;
    private final OrderProductService orderProductService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public BaseOrderResponse getById(Long customerId, UUID orderId) {
        var orderExists = orderRepository.existsByIdAndCustomerId(orderId, customerId);

        if (!orderExists) {
            throw new ResourceNotFoundException(OrderEntity.class, orderId);
        }

        var result =
                orderProductService.getOrderedProducts(orderId);

        return orderMapper.toResponse(orderId, result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public UUID create(Long customerId, CreateOrderCommandInfo createCommand) {
        final var customer = customerService.getById(customerId);
        validateCustomerIsActive(customer);
        var orderEntity = assembleOrder(customer, createCommand);

        return orderRepository.saveAndFlush(orderEntity).getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public UUID update(Long customerId, UpdateOrderCommandInfo updateCommand) {
        final var orderEntity = getOrderForUpdate(updateCommand.orderId());
        validateOrderOwnership(orderEntity.getCustomer().getId(), customerId);
        validateOrderForUpdate(orderEntity);

        var existingProducts = orderEntity.getOrderProducts();
        var updateMap = updateCommand.productQuantities();

        var updateExistingReportMap =
                updateExistingDependencies(existingProducts, updateMap);

        var problemsWhileUpdateMap =
                checkForProblems(updateExistingReportMap);


        var hasNewProductsToOrder = updateExistingReportMap.size() < updateMap.size();

        if (hasNewProductsToOrder) {
            var newProductsToOrderMap =
                    filterUpdatedProducts(updateExistingReportMap.keySet(), updateMap);

            var reserveResponse =
                    reserveProducts(newProductsToOrderMap);

            if (reserveResponse.hasProblems()) {
                problemsWhileUpdateMap.putAll(reserveResponse.problemsMap());
            }

            var createdOrderProductEntities = createOrderProducts(reserveResponse);
            orderEntity.addOrderProducts(createdOrderProductEntities);
        }

        if (!problemsWhileUpdateMap.isEmpty()) {
            throw new ReservationException(problemsWhileUpdateMap);
        }

        return orderEntity.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void cancel(Long customerId, UUID orderId) {
        final var order = getOrderForCancel(orderId);
        validateOrderOwnership(order.getCustomer().getId(), customerId);
        validateOrderCanBeCancelled(order);
        cancelOrder(order);
        returnProductsToStock(order.getOrderProducts());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void confirm(Long customerId, UUID orderId) {
        //TODO: stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void updateOrderStatus(UUID orderId, OrderStatus status) {
        final var orderEntity = getOrderForUpdateOrderStatus(orderId);
        orderEntity.setStatus(status);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<UUID, List<OrderInfo>> getOrdersDetailedInfosByProductIds(List<UUID> productIds) {
        var allowedStatuses = List.of(OrderStatus.CREATED, OrderStatus.CONFIRMED);
        var orders = orderRepository.findAllByProductIdIn(productIds, allowedStatuses);

        if (orders.isEmpty()) {
            return Collections.emptyMap();
        }

        var detailedOrderContext = orders.stream()
                .collect(Collectors.teeing(
                        Collectors.toMap(
                                it -> it.getCustomer().getId(),
                                it -> it.getCustomer().getLogin(),
                                (existing, replacement) -> existing
                        ),

                        Collectors.groupingBy(
                                it -> it.getRequestedProduct().getId().getProductId()
                        ),

                        DetailedOrderContext::new
                ));

        var customerExternalData =
                fetchCustomerExternalDataByLoginsMap(detailedOrderContext.customerIdToLoginMap());

        return orderMapper.toResponse(
                detailedOrderContext.groupedByProductIdProjectionMap(),
                customerExternalData
        );
    }

    private CustomerExternalData fetchCustomerExternalDataByLoginsMap(Map<Long, String> idLoginMap) {
        return customerService.getExternalDataByLogins(idLoginMap);
    }

    // ---------------------------------- <CREATE> HELPER METHODS ------------------------------------------------------

    private OrderEntity assembleOrder(BaseCustomerInfoDto customerDto, CreateOrderCommandInfo createCommand) {
        final var productReservationResponse
                = reserveProducts(createCommand.productQuantities());

        if (productReservationResponse.hasProblems()) {
            throw new ReservationException(productReservationResponse.problemsMap());
        }

        final var orderProductEntities
                = createOrderProducts(productReservationResponse);

        final var orderEntity = orderMapper.toEntity(customerDto.id(), createCommand.deliveryAddress());
        orderEntity.addOrderProducts(orderProductEntities);

        return orderEntity;
    }

    // ---------------------------------- <CANCEL> HELPER METHODS ------------------------------------------------------

    private OrderEntity getOrderForCancel(UUID orderId) {
        return orderRepository.findByIdForUpdate(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(OrderEntity.class, orderId));
    }

    private void validateOrderCanBeCancelled(OrderEntity order) {
        final var currentStatus = order.getStatus();
        if (!currentStatus.equals(OrderStatus.CREATED)) {
            throw new OrderCannotBeCancelledException(order.getId(), currentStatus);
        }
    }

    private void cancelOrder(OrderEntity order) {
        order.setStatus(OrderStatus.CANCELLED);
    }

    private void returnProductsToStock(List<OrderProductEntity> orderProducts) {
        orderProducts.forEach(orderProduct -> {
            var orderedQuantity = orderProduct.getOrderedQuantity();
            var linkedProductEntity = orderProduct.getProduct();
            var currentProductQuantity = linkedProductEntity.getQuantity();
            linkedProductEntity.setQuantity(currentProductQuantity.add(orderedQuantity));
        });
    }

    // ---------------------------------- <UPDATE> HELPER METHODS ------------------------------------------------------
    private OrderEntity getOrderForUpdateOrderStatus(UUID orderId) {
        return orderRepository.findById(orderId).orElseThrow(
                () -> new ResourceNotFoundException(OrderEntity.class, orderId)
        );
    }

    private OrderEntity getOrderForUpdate(UUID orderId) {
        return orderRepository.findByIdForUpdate(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(OrderEntity.class, orderId));
    }

    private void validateOrderForUpdate(OrderEntity order) {
        if (order.getStatus() != OrderStatus.CREATED) {
            throw new OrderCannotBeUpdatedException(order.getId(), order.getStatus());
        }
    }

    private Map<UUID, ReservationStatus> updateExistingDependencies(
            List<OrderProductEntity> existingProducts,
            Map<UUID, BigDecimal> updateMap
    ) {
        var reportMap = new HashMap<UUID, ReservationStatus>();

        existingProducts.forEach(orderProductEntity -> {
            var productEntityId = orderProductEntity.getId().getProductId();
            var isUpdateNeeded = updateMap.containsKey(productEntityId);

            if (isUpdateNeeded) {
                var currentProductQuantity = orderProductEntity.getProduct().getQuantity();
                var deltaQuantity = updateMap.get(orderProductEntity.getId().getProductId());

                var isEnoughInStock = currentProductQuantity.compareTo(deltaQuantity) >= 0;

                if (isEnoughInStock) {
                    increaseProductReserve(orderProductEntity.getProduct(), deltaQuantity);
                    increaseOrderProductQuantity(orderProductEntity, deltaQuantity);
                    reportMap.put(productEntityId, ReservationStatus.COMPLETE);
                } else {
                    reportMap.put(productEntityId, ReservationStatus.NOT_ENOUGH_QUANTITY);
                }
            }
        });

        return reportMap;
    }

    private Map<UUID, ReservationStatus> checkForProblems(Map<UUID, ReservationStatus> reportMap) {
        return reportMap.entrySet().stream()
                .filter(entry -> entry.getValue() != ReservationStatus.COMPLETE)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }

    private Map<UUID, BigDecimal> filterUpdatedProducts(Set<UUID> alreadyUpdatedIds, Map<UUID, BigDecimal> updateMap) {
        var newProductsProductQuantities = new HashMap<>(updateMap);
        newProductsProductQuantities.keySet().removeAll(alreadyUpdatedIds);

        return newProductsProductQuantities;
    }

    private void increaseOrderProductQuantity(OrderProductEntity orderProductEntity, BigDecimal deltaQuantity) {
        var currentOrderProductQuantity = orderProductEntity.getOrderedQuantity();
        var actualProductPrice = orderProductEntity.getProduct().getPrice();

        orderProductEntity.setOrderedQuantity(currentOrderProductQuantity.add(deltaQuantity));
        orderProductEntity.setPrice(actualProductPrice);
    }

    private void increaseProductReserve(ProductEntity product, BigDecimal deltaQuantity) {
        var currentProductQuantity = product.getQuantity();
        product.setQuantity(currentProductQuantity.subtract(deltaQuantity));
    }

    // ---------------------------------- <CREATE> / <UPDATE> USAGE ----------------------------------------------------

    private List<OrderProductEntity> createOrderProducts(
            ProductReservationResponse reservationResponse
    ) {
        return reservationResponse.reservedProductsInfoMap().entrySet().stream()
                .map(it -> orderProductMapper.toEntity(it.getKey(), it.getValue()))
                .toList();
    }

    private ProductReservationResponse reserveProducts(Map<UUID, BigDecimal> productQuantities) {
        final var reservationCommand
                = productReservationMapper.toReserveCommand(productQuantities);

        return productService.reserveProductsWithLock(reservationCommand);
    }

    // ------------------------------- SECURITY HELPER METHODS ---------------------------------------------------------

    private void validateOrderOwnership(Long orderCustomerId, Long headerCustomerId) {
        if (!orderCustomerId.equals(headerCustomerId)) {
            throw new NotYourOrderException(headerCustomerId);
        }
    }

    private void validateCustomerIsActive(BaseCustomerInfoDto customer) {
        if (!customer.isActive()) {
            throw new CustomerBannedException(customer.id());
        }
    }
}
