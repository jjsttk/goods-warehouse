package com.jjsttk.goodswarehouse.mapper.product.spring;

import com.jjsttk.goodswarehouse.mapper.product.ProductReservationConverter;
import com.jjsttk.goodswarehouse.service.product.dto.command.ProductServiceReservationCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public final class ConversionServiceProductReservationConverter implements ProductReservationConverter {

    @Override
    public ProductServiceReservationCommand toReserveCommand(Map<UUID, BigDecimal> productQuantities) {
        return ProductServiceReservationCommand.builder()
                .productQuantities(productQuantities)
                .build();
    }
}
