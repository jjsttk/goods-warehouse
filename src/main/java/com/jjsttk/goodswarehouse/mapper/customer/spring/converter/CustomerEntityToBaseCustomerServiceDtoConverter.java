package com.jjsttk.goodswarehouse.mapper.customer.spring.converter;

import com.jjsttk.goodswarehouse.persistence.entity.customer.CustomerEntity;
import com.jjsttk.goodswarehouse.service.customer.dto.response.BaseCustomerServiceDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public final class CustomerEntityToBaseCustomerServiceDtoConverter
        implements Converter<CustomerEntity, BaseCustomerServiceDto> {

    @Override
    public BaseCustomerServiceDto convert(@NonNull CustomerEntity source) {
        return BaseCustomerServiceDto.builder()
                .id(source.getId())
                .email(source.getEmail())
                .login(source.getLogin())
                .isActive(source.getIsActive())
                .build();
    }
}
