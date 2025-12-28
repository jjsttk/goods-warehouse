package com.jjsttk.goodswarehouse.service.order;

import com.jjsttk.goodswarehouse.exception.service.ResourceNotFoundException;
import com.jjsttk.goodswarehouse.exception.service.customer.CustomerBannedException;
import com.jjsttk.goodswarehouse.exception.service.order.NotYourOrderException;
import com.jjsttk.goodswarehouse.exception.service.order.OrderCannotBeCancelledException;
import com.jjsttk.goodswarehouse.exception.service.order.OrderCannotBeUpdatedException;
import com.jjsttk.goodswarehouse.exception.service.order.NotEnoughQuantityInStockException;
import com.jjsttk.goodswarehouse.mapper.order.OrderServiceConverter;
import com.jjsttk.goodswarehouse.mapper.order.product.OrderProductConverter;
import com.jjsttk.goodswarehouse.mapper.product.ProductReservationConverter;
import com.jjsttk.goodswarehouse.persistence.entity.order.OrderEntity;
import com.jjsttk.goodswarehouse.persistence.entity.order.product.OrderProductEntity;
import com.jjsttk.goodswarehouse.persistence.entity.product.ProductEntity;
import com.jjsttk.goodswarehouse.persistence.repository.OrderRepository;
import com.jjsttk.goodswarehouse.service.customer.CustomerService;
import com.jjsttk.goodswarehouse.service.customer.dto.response.BaseCustomerServiceDto;
import com.jjsttk.goodswarehouse.service.order.dto.command.OrderServiceCreateCommand;
import com.jjsttk.goodswarehouse.service.order.dto.command.OrderServiceUpdateCommand;
import com.jjsttk.goodswarehouse.service.order.dto.response.BaseOrderServiceResponse;
import com.jjsttk.goodswarehouse.service.order.product.OrderProductService;
import com.jjsttk.goodswarehouse.service.product.ProductService;
import com.jjsttk.goodswarehouse.service.product.dto.response.ProductServiceReservationResponse;
import com.jjsttk.goodswarehouse.shared.enums.order.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    public BaseOrderServiceResponse getById(Long customerId, UUID orderId) {
        var orderExists = orderRepository.existsByIdAndCustomerId(orderId, customerId);

        if (!orderExists) {
            throw new ResourceNotFoundException(OrderEntity.class, orderId);
        }

        final var summaries =
                orderProductService.getOrderedProducts(orderId);

        return orderMapper.toResponse(orderId, summaries);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public UUID create(Long customerId, OrderServiceCreateCommand createCommand) {
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
    public UUID update(Long customerId, OrderServiceUpdateCommand updateCommand) {
        final var orderEntity = getOrderForUpdate(updateCommand.orderId());
        validateOrderOwnership(orderEntity.getCustomer().getId(), customerId);
        validateOrderForUpdate(orderEntity);

        var existingProducts = orderEntity.getOrderProducts();
        var updateMap = updateCommand.productQuantities();

        var updatedIds = updateExistingProducts(existingProducts, updateMap);

        var hasNewProductsToOrder = updatedIds.size() < updateMap.size();

        if (hasNewProductsToOrder) {
            var newProductsToOrderMap = filterUpdatedProducts(updatedIds, updateMap);
            var reserveResponse = reserveProducts(newProductsToOrderMap);
            var createdOrderProductEntities = createOrderProducts(reserveResponse);
            orderEntity.addOrderProducts(createdOrderProductEntities);
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
        //TODO: stub nop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void updateOrderStatus(Long customerIdHeader, UUID orderId, OrderStatus status) {
        final var orderEntity = getOrderForUpdateOrderStatus(orderId);
        validateOrderOwnership(orderEntity.getCustomer().getId(), customerIdHeader);
        orderEntity.setStatus(status);
    }

    // ---------------------------------- <CREATE> HELPER METHODS ------------------------------------------------------

    private OrderEntity assembleOrder(BaseCustomerServiceDto customerDto, OrderServiceCreateCommand createCommand) {
        final var productReservationResponse
                = reserveProducts(createCommand.productQuantities());

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


    private Set<UUID> updateExistingProducts(
            List<OrderProductEntity> existingProducts,
            Map<UUID, BigDecimal> updateMap
    ) {
        return existingProducts.stream()
                .filter(entity -> updateMap.containsKey(entity.getId().getProductId()))
                .peek(orderProductEntity -> {
                    var deltaQuantity = updateMap.get(orderProductEntity.getId().getProductId());
                    increaseProductReserveOrThrow(orderProductEntity.getProduct(), deltaQuantity);
                    increaseOrderProductQuantity(orderProductEntity, deltaQuantity);
                })
                .map(orderProductEntity -> orderProductEntity.getId().getProductId())
                .collect(Collectors.toSet());
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

    private void increaseProductReserveOrThrow(ProductEntity product, BigDecimal deltaQuantity) {
        var currentProductQuantity = product.getQuantity();
        var isEnoughInStock = currentProductQuantity.compareTo(deltaQuantity) >= 0;

        if (!isEnoughInStock) {
            throw new NotEnoughQuantityInStockException(product.getId());
        }

        product.setQuantity(currentProductQuantity.subtract(deltaQuantity));
    }

    // ---------------------------------- <CREATE> / <UPDATE> USAGE ----------------------------------------------------

    private List<OrderProductEntity> createOrderProducts(
            ProductServiceReservationResponse reservationResponse
    ) {
        return reservationResponse.productInfo().entrySet().stream()
                .map(it -> orderProductMapper.toEntity(it.getKey(), it.getValue()))
                .toList();
    }

    private ProductServiceReservationResponse reserveProducts(Map<UUID, BigDecimal> productQuantities) {
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

    private void validateCustomerIsActive(BaseCustomerServiceDto customer) {
        if (!customer.isActive()) {
            throw new CustomerBannedException(customer.id());
        }
    }
}
