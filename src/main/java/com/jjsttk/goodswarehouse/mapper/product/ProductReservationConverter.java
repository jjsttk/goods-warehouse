package com.jjsttk.goodswarehouse.mapper.product;

import com.jjsttk.goodswarehouse.service.product.dto.command.ReserveProductCommandInfo;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public interface ProductReservationConverter {

    ReserveProductCommandInfo toReserveCommand(Map<UUID, BigDecimal> productQuantities);

}
