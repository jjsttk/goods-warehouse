package com.jjsttk.goodswarehouse.mapper.product;

import com.jjsttk.goodswarehouse.persistence.entity.product.ProductEntity;
import com.jjsttk.goodswarehouse.service.product.dto.command.CreateProductCommandInfo;
import com.jjsttk.goodswarehouse.service.product.dto.command.UpdateProductCommandInfo;
import com.jjsttk.goodswarehouse.service.product.dto.response.ProductDetailedResponse;
import com.jjsttk.goodswarehouse.service.product.dto.response.ProductReservationResponse;
import com.jjsttk.goodswarehouse.service.product.dto.response.ReservedProductInfo;
import com.jjsttk.goodswarehouse.shared.enums.product.ReservationStatus;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public interface ProductServiceConverter {

    ProductEntity toEntity(CreateProductCommandInfo createProductCommandInfo);

    void update(ProductEntity entity, UpdateProductCommandInfo updateProductCommandInfo);

    ReservedProductInfo toProductInfo(BigDecimal valueToReserve, BigDecimal price);

    ProductDetailedResponse toResponse(ProductEntity entity);

    ProductReservationResponse toResponse(
            Map<UUID, ReservedProductInfo> reservedProductsResponseMap,
            Map<UUID, ReservationStatus> problemMap
    );

}
