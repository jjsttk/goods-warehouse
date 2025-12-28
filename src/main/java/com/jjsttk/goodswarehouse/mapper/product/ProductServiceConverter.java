package com.jjsttk.goodswarehouse.mapper.product;

import com.jjsttk.goodswarehouse.persistence.entity.product.ProductEntity;
import com.jjsttk.goodswarehouse.service.product.dto.command.ProductServiceCreateCommand;
import com.jjsttk.goodswarehouse.service.product.dto.command.ProductServiceUpdateCommand;
import com.jjsttk.goodswarehouse.service.product.dto.response.ProductServiceProductDetailedResponse;
import com.jjsttk.goodswarehouse.service.product.dto.response.ProductServiceReservationResponse;
import com.jjsttk.goodswarehouse.service.product.dto.response.ProductServiceReservedProductInfo;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public interface ProductServiceConverter {

    ProductEntity toEntity(ProductServiceCreateCommand productServiceCreateCommand);

    void update(ProductEntity entity, ProductServiceUpdateCommand productServiceUpdateCommand);

    ProductServiceReservedProductInfo toProductInfo(BigDecimal valueToReserve, BigDecimal price);

    ProductServiceProductDetailedResponse toResponse(ProductEntity entity);

    ProductServiceReservationResponse toResponse(
            Map<UUID, ProductServiceReservedProductInfo> reservedProductsResponseMap
    );

}
