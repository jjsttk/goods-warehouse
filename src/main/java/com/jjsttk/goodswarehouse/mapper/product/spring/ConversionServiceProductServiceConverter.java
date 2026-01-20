package com.jjsttk.goodswarehouse.mapper.product.spring;

import com.jjsttk.goodswarehouse.mapper.product.ProductServiceConverter;
import com.jjsttk.goodswarehouse.persistence.entity.product.ProductEntity;
import com.jjsttk.goodswarehouse.service.product.dto.command.CreateProductCommandInfo;
import com.jjsttk.goodswarehouse.service.product.dto.command.UpdateProductCommandInfo;
import com.jjsttk.goodswarehouse.service.product.dto.response.ProductDetailedResponse;
import com.jjsttk.goodswarehouse.service.product.dto.response.ProductReservationResponse;
import com.jjsttk.goodswarehouse.service.product.dto.response.ReservedProductInfo;
import com.jjsttk.goodswarehouse.shared.enums.product.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public final class ConversionServiceProductServiceConverter implements ProductServiceConverter {
    private final ConversionService conversionService;

    @Override
    public ProductEntity toEntity(
            CreateProductCommandInfo createCommandDtO
    ) {
        return ProductEntity.builder()
                .name(createCommandDtO.name())
                .article(createCommandDtO.article())
                .description(createCommandDtO.description())
                .category(createCommandDtO.category())
                .price(createCommandDtO.price())
                .quantity(createCommandDtO.quantity())
                .isAvailable(createCommandDtO.isAvailable())
                .build();
    }

    @Override
    public ProductDetailedResponse toResponse(
            ProductEntity entity
    ) {
        return Objects.requireNonNull(conversionService.convert(entity, ProductDetailedResponse.class));
    }

    @Override
    public ProductReservationResponse toResponse(
            Map<UUID, ReservedProductInfo> reservedProductsResponseMap,
            Map<UUID, ReservationStatus> reservationProblemMap
    ) {
        return ProductReservationResponse.builder()
                .reservedProductsInfoMap(reservedProductsResponseMap)
                .problemsMap(reservationProblemMap)
                .build();
    }

    public ReservedProductInfo toResponse(
            BigDecimal reservedQuantity,
            BigDecimal price
    ) {
        return ReservedProductInfo.builder()
                .reservedQuantity(reservedQuantity)
                .priceAtMoment(price)
                .build();
    }

    @Override
    public ReservedProductInfo toProductInfo(
            BigDecimal valueToReserve,
            BigDecimal price
    ) {
        return ReservedProductInfo.builder()
                .reservedQuantity(valueToReserve)
                .priceAtMoment(price)
                .build();
    }

    @Override
    public void update(
            ProductEntity entity,
            UpdateProductCommandInfo updateProductCommandInfo
    ) {
        updateEntity(entity, updateProductCommandInfo);
    }

    private void updateEntity(
            ProductEntity entity,
            UpdateProductCommandInfo updateProductCommandInfo
    ) {
        var nameFromUpdate = updateProductCommandInfo.name();
        if (nameFromUpdate != null
            && !Objects.equals(nameFromUpdate, entity.getName())) {
            entity.setName(updateProductCommandInfo.name());
        }

        var descriptionFromUpdate = updateProductCommandInfo.description();
        if (descriptionFromUpdate != null
            && !Objects.equals(descriptionFromUpdate, entity.getDescription())) {
            entity.setDescription(descriptionFromUpdate);
        }

        var articleFromUpdate = updateProductCommandInfo.article();
        if (articleFromUpdate != null
            && !Objects.equals(articleFromUpdate, entity.getArticle())) {
            entity.setArticle(articleFromUpdate);
        }

        var priceFromUpdate = updateProductCommandInfo.price();
        if (priceFromUpdate != null && priceFromUpdate.compareTo(entity.getPrice()) != 0) {
            entity.setPrice(priceFromUpdate);
        }

        var categoryFromUpdate = updateProductCommandInfo.category();
        if (categoryFromUpdate != null && !Objects.equals(categoryFromUpdate, entity.getCategory())) {
            entity.setCategory(categoryFromUpdate);
        }

        var isAvailableFromUpdate = updateProductCommandInfo.isAvailable();
        if (isAvailableFromUpdate != null && !Objects.equals(isAvailableFromUpdate, entity.getIsAvailable())) {
            entity.setIsAvailable(isAvailableFromUpdate);
        }

        var quantityFromUpdate = updateProductCommandInfo.quantity();
        if (quantityFromUpdate != null && quantityFromUpdate.compareTo(entity.getQuantity()) != 0) {
            entity.setQuantity(quantityFromUpdate);
        }
    }
}





