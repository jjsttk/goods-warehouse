package com.jjsttk.goodswarehouse.mapper.customer.spring.converter;

import com.jjsttk.goodswarehouse.persistence.entity.customer.CustomerEntity;
import com.jjsttk.goodswarehouse.service.customer.dto.response.BaseCustomerInfoDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public final class CustomerEntityToBaseCustomerInfoDtoConverter
        implements Converter<CustomerEntity, BaseCustomerInfoDto> {

    @Override
    public BaseCustomerInfoDto convert(@NonNull CustomerEntity source) {
        return BaseCustomerInfoDto.builder()
                .id(source.getId())
                .email(source.getEmail())
                .login(source.getLogin())
                .isActive(source.getIsActive())
                .build();
    }
}
