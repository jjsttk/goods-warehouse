package com.jjsttk.goodswarehouse.service.order.product;

import com.jjsttk.goodswarehouse.mapper.order.product.OrderProductConverter;
import com.jjsttk.goodswarehouse.persistence.repository.OrderProductRepository;
import com.jjsttk.goodswarehouse.service.order.product.dto.response.OrderProductProjection;
import com.jjsttk.goodswarehouse.shared.enums.order.OrderStatus;
import com.jjsttk.goodswarehouse.testutil.OrderProductTestDataFactory;
import com.jjsttk.goodswarehouse.testutil.OrderTestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderProductServiceImplTest {

    @Mock
    private OrderProductRepository repositoryMock;

    @Mock
    private OrderProductConverter mapperMock;

    @InjectMocks
    private OrderProductServiceImpl sut;

    @Test
    void getOrderedProducts() {
        var orderEntityStub =
                OrderTestDataFactory.getOrderEntityWithIdByLengthAndStatus(
                        5,
                        OrderStatus.CREATED,
                        BigDecimal.ONE,
                        true
                );

        var responseContainerStub =
                OrderProductTestDataFactory.getServiceResponseBasedEntity(orderEntityStub);

        var summariesListStub =
                (List<OrderProductProjection>) responseContainerStub.orderProducts();

        when(repositoryMock.findProductSummariesByOrderId(orderEntityStub.getId()))
                .thenReturn(summariesListStub);
        when(mapperMock.toResponse(summariesListStub))
                .thenReturn(responseContainerStub);

        var res =
                sut.getOrderedProducts(orderEntityStub.getId());

        assertEquals(res.orderProducts().size(), orderEntityStub.getOrderProducts().size());

        verify(repositoryMock).findProductSummariesByOrderId(orderEntityStub.getId());
        verify(mapperMock).toResponse(summariesListStub);
    }
}
