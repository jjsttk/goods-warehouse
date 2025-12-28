package com.jjsttk.goodswarehouse.mapper.product;

import com.jjsttk.goodswarehouse.service.product.dto.command.ProductServiceReservationCommand;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public interface ProductReservationConverter {

    ProductServiceReservationCommand toReserveCommand(Map<UUID, BigDecimal> productQuantities);

}
