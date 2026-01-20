package com.jjsttk.goodswarehouse.service.order.product;

import com.jjsttk.goodswarehouse.mapper.order.product.OrderProductConverter;
import com.jjsttk.goodswarehouse.persistence.repository.OrderProductRepository;
import com.jjsttk.goodswarehouse.service.order.product.dto.response.OrderProductProjection;
import com.jjsttk.goodswarehouse.service.order.product.dto.response.OrderProductResponseContainer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderProductServiceImpl implements OrderProductService {
    private final OrderProductRepository repository;
    private final OrderProductConverter mapper;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public OrderProductResponseContainer<OrderProductProjection> getOrderedProducts(
            UUID orderId
    ) {
        var result = repository.findProductSummariesByOrderId(orderId);
        return mapper.toResponse(result);
    }
}
