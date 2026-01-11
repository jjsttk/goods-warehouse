package com.jjsttk.goodswarehouse.mapper.product.spring;

import com.jjsttk.goodswarehouse.mapper.product.ProductServiceConverter;
import com.jjsttk.goodswarehouse.persistence.entity.product.ProductEntity;
import com.jjsttk.goodswarehouse.service.product.dto.command.ProductServiceCreateCommand;
import com.jjsttk.goodswarehouse.service.product.dto.command.ProductServiceUpdateCommand;
import com.jjsttk.goodswarehouse.service.product.dto.response.ProductServiceProductDetailedResponse;
import com.jjsttk.goodswarehouse.service.product.dto.response.ProductServiceReservationResponse;
import com.jjsttk.goodswarehouse.service.product.dto.response.ProductServiceReservedProductInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.lang.NonNull;
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
            @NonNull ProductServiceCreateCommand createCommandDtO
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
    public ProductServiceProductDetailedResponse toResponse(
            @NonNull ProductEntity entity
    ) {
        return conversionService.convert(entity, ProductServiceProductDetailedResponse.class);
    }

    @Override
    public ProductServiceReservationResponse toResponse(
            @NonNull Map<UUID, ProductServiceReservedProductInfo> reservedProductsResponseMap
    ) {
        return ProductServiceReservationResponse.builder()
                .productInfo(reservedProductsResponseMap)
                .build();
    }

    public ProductServiceReservedProductInfo toResponse(
            @NonNull BigDecimal reservedQuantity,
            @NonNull BigDecimal price
    ) {
        return ProductServiceReservedProductInfo.builder()
                .reservedQuantity(reservedQuantity)
                .priceAtMoment(price)
                .build();
    }

    @Override
    public ProductServiceReservedProductInfo toProductInfo(
            @NonNull BigDecimal valueToReserve,
            @NonNull BigDecimal price
    ) {
        return ProductServiceReservedProductInfo.builder()
                .reservedQuantity(valueToReserve)
                .priceAtMoment(price)
                .build();
    }

    @Override
    public void update(
            @NonNull ProductEntity entity,
            @NonNull ProductServiceUpdateCommand productServiceUpdateCommand
    ) {
        updateEntity(entity, productServiceUpdateCommand);
    }

    private void updateEntity(
            @NonNull ProductEntity entity,
            @NonNull ProductServiceUpdateCommand productServiceUpdateCommand
    ) {
        var nameFromUpdate = productServiceUpdateCommand.name();
        if (nameFromUpdate != null
            && !Objects.equals(nameFromUpdate, entity.getName())) {
            entity.setName(productServiceUpdateCommand.name());
        }

        var descriptionFromUpdate = productServiceUpdateCommand.description();
        if (descriptionFromUpdate != null
            && !Objects.equals(descriptionFromUpdate, entity.getDescription())) {
            entity.setDescription(descriptionFromUpdate);
        }

        var articleFromUpdate = productServiceUpdateCommand.article();
        if (articleFromUpdate != null
            && !Objects.equals(articleFromUpdate, entity.getArticle())) {
            entity.setArticle(articleFromUpdate);
        }

        var priceFromUpdate = productServiceUpdateCommand.price();
        if (priceFromUpdate != null && priceFromUpdate.compareTo(entity.getPrice()) != 0) {
            entity.setPrice(priceFromUpdate);
        }

        var categoryFromUpdate = productServiceUpdateCommand.category();
        if (categoryFromUpdate != null && !Objects.equals(categoryFromUpdate, entity.getCategory())) {
            entity.setCategory(categoryFromUpdate);
        }

        var isAvailableFromUpdate = productServiceUpdateCommand.isAvailable();
        if (isAvailableFromUpdate != null && !Objects.equals(isAvailableFromUpdate, entity.getIsAvailable())) {
            entity.setIsAvailable(isAvailableFromUpdate);
        }

        var quantityFromUpdate = productServiceUpdateCommand.quantity();
        if (quantityFromUpdate != null && quantityFromUpdate.compareTo(entity.getQuantity()) != 0) {
            entity.setQuantity(quantityFromUpdate);
        }
    }
}





