package com.jjsttk.goodswarehouse.service.customer;

import com.jjsttk.goodswarehouse.exception.service.ResourceNotFoundException;
import com.jjsttk.goodswarehouse.mapper.customer.spring.ConversionServiceCustomerMapper;
import com.jjsttk.goodswarehouse.persistence.entity.customer.CustomerEntity;
import com.jjsttk.goodswarehouse.persistence.repository.CustomerRepository;
import com.jjsttk.goodswarehouse.service.customer.dto.response.BaseCustomerServiceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository repository;
    private final ConversionServiceCustomerMapper mapper;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public BaseCustomerServiceDto getById(Long id) {
        var mbCustomer = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CustomerEntity.class, id));
        return mapper.mapToServiceResponse(mbCustomer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Boolean existsById(Long id) {
        return repository.existsById(id);
    }
}
