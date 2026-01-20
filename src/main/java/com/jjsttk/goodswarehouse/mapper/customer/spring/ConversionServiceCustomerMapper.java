package com.jjsttk.goodswarehouse.mapper.customer.spring;

import com.jjsttk.goodswarehouse.mapper.customer.CustomerConverter;
import com.jjsttk.goodswarehouse.persistence.entity.customer.CustomerEntity;
import com.jjsttk.goodswarehouse.service.customer.dto.response.BaseCustomerInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.util.Objects;

@RequiredArgsConstructor
@Component
public final class ConversionServiceCustomerMapper implements CustomerConverter {
    private final ConversionService conversionService;

    @Override
    public BaseCustomerInfoDto mapToServiceResponse(CustomerEntity entity) {
        return Objects.requireNonNull(conversionService.convert(entity, BaseCustomerInfoDto.class));
    }
}
