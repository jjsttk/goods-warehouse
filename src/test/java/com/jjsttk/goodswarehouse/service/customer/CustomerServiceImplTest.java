package com.jjsttk.goodswarehouse.service.customer;

import com.jjsttk.goodswarehouse.exception.service.ResourceNotFoundException;
import com.jjsttk.goodswarehouse.mapper.customer.spring.ConversionServiceCustomerMapper;
import com.jjsttk.goodswarehouse.persistence.entity.customer.CustomerEntity;
import com.jjsttk.goodswarehouse.persistence.repository.CustomerRepository;
import com.jjsttk.goodswarehouse.service.customer.dto.response.BaseCustomerServiceDto;
import com.jjsttk.goodswarehouse.testutil.CustomerTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @InjectMocks
    private CustomerServiceImpl sut;

    @Mock
    private CustomerRepository repositoryMock;

    @Mock
    private ConversionServiceCustomerMapper mapperMock;

    private CustomerEntity customerEntityStub;
    private BaseCustomerServiceDto baseCustomerServiceDtoStub;

    @BeforeEach
    void setUp() {
        customerEntityStub =
                CustomerTestDataFactory.getCustomerEntityWithGeneratedId(true);
        baseCustomerServiceDtoStub =
                CustomerTestDataFactory.getCustomerServiceDtoFromCustomerEntity(customerEntityStub);
    }

    @Test
    void getByIdHappyPath() {
        var id = customerEntityStub.getId();
        when(repositoryMock.findById(id))
                .thenReturn(Optional.of(customerEntityStub));
        when(mapperMock.mapToServiceResponse(customerEntityStub))
                .thenReturn(baseCustomerServiceDtoStub);

        sut.getById(id);

        verify(repositoryMock, times(1)).findById(id);
        verify(mapperMock, times(1)).mapToServiceResponse(customerEntityStub);
    }

    @Test
    void getByIdNotFound() {
        var id = customerEntityStub.getId();

        when(repositoryMock.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> sut.getById(id));

        verify(repositoryMock).findById(id);
        verify(mapperMock, never()).mapToServiceResponse(any());
    }

    @Test
    void existsByIdShouldReturnTrue() {
        var id = customerEntityStub.getId();
        when(repositoryMock.existsById(customerEntityStub.getId()))
                .thenReturn(true);

        var res = sut.existsById(id);

        assertThat(res).isTrue();
        verify(repositoryMock, times(1)).existsById(id);

    }

    @Test
    void existsByIdShouldReturnFalse() {
        var id = customerEntityStub.getId();
        when(repositoryMock.existsById(customerEntityStub.getId()))
                .thenReturn(false);

        var res = sut.existsById(id);

        assertThat(res).isFalse();
        verify(repositoryMock, times(1)).existsById(customerEntityStub.getId());
    }
}
