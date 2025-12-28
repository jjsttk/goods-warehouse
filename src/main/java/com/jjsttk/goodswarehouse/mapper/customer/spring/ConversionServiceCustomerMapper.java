package com.jjsttk.goodswarehouse.mapper.customer.spring;

import com.jjsttk.goodswarehouse.mapper.customer.CustomerConverter;
import com.jjsttk.goodswarehouse.persistence.entity.customer.CustomerEntity;
import com.jjsttk.goodswarehouse.service.customer.dto.response.BaseCustomerServiceDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public final class ConversionServiceCustomerMapper implements CustomerConverter {
    private final ConversionService conversionService;

    @Override
    public BaseCustomerServiceDto mapToServiceResponse(@NonNull CustomerEntity entity) {
        return conversionService.convert(entity, BaseCustomerServiceDto.class);
    }
}
