package com.jjsttk.goodswarehouse.testutil;

import com.jjsttk.goodswarehouse.controller.product.dto.request.CreateProductRequest;
import com.jjsttk.goodswarehouse.controller.product.dto.response.GetProductResponse;
import com.jjsttk.goodswarehouse.controller.product.dto.response.PageGetProductResponse;
import com.jjsttk.goodswarehouse.persistence.entity.product.ProductEntity;
import com.jjsttk.goodswarehouse.service.product.dto.command.CreateProductCommandInfo;
import com.jjsttk.goodswarehouse.service.product.dto.command.ReserveProductCommandInfo;
import com.jjsttk.goodswarehouse.service.product.dto.response.ProductDetailedResponse;
import com.jjsttk.goodswarehouse.service.product.dto.response.ProductReservationResponse;
import com.jjsttk.goodswarehouse.service.product.dto.response.ReservedProductInfo;
import com.jjsttk.goodswarehouse.shared.enums.exchange.PriceCurrency;
import org.instancio.Instancio;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.instancio.Select.field;


public class ProductTestDataFactory {

    public static ProductEntity getProductEntityWithoutGeneratedId() {
        return Instancio.of(ProductEntity.class)
                .ignore(field("id"))
                .set(field("createdAt"), LocalDate.now())
                .set(field("lastQuantityModified"), OffsetDateTime.now())
                .create();
    }

    public static ProductEntity getProductEntityWithGeneratedId() {
        return Instancio.of(ProductEntity.class).create();
    }

    public static CreateProductRequest getCreateProductRequest(ProductEntity productEntity) {
        return Instancio.of(CreateProductRequest.class)
                .set(field("name"), productEntity.getName())
                .set(field("article"), productEntity.getArticle())
                .set(field("category"), productEntity.getCategory())
                .set(field("quantity"), productEntity.getQuantity())
                .set(field("price"), productEntity.getPrice())
                .set(field("description"), productEntity.getDescription())
                .create();
    }

    public static GetProductResponse getGetProductResponse(ProductEntity productEntity) {
        return Instancio.of(GetProductResponse.class)
                .set(field("id"), productEntity.getId())
                .set(field("name"), productEntity.getName())
                .set(field("article"), productEntity.getArticle())
                .set(field("category"), productEntity.getCategory())
                .set(field("quantity"), productEntity.getQuantity())
                .set(field("price"), productEntity.getPrice())
                .set(field("description"), productEntity.getDescription())
                .set(field("currency"), PriceCurrency.RUB)
                .set(field("createdAt"), productEntity.getCreatedAt())
                .set(field("lastQuantityModified"), productEntity.getLastQuantityModified())
                .create();
    }

    public static CreateProductCommandInfo getCreateProductCommand(ProductEntity productEntity) {
        return Instancio.of(CreateProductCommandInfo.class)
                .set(field("name"), productEntity.getName())
                .set(field("article"), productEntity.getArticle())
                .set(field("category"), productEntity.getCategory())
                .set(field("quantity"), productEntity.getQuantity())
                .set(field("price"), productEntity.getPrice())
                .set(field("description"), productEntity.getDescription())
                .create();
    }

    public static ProductDetailedResponse getProductDetailedResponse(ProductEntity productEntity) {
        return Instancio.of(ProductDetailedResponse.class)
                .set(field("id"), productEntity.getId())
                .set(field("name"), productEntity.getName())
                .set(field("article"), productEntity.getArticle())
                .set(field("category"), productEntity.getCategory())
                .set(field("quantity"), productEntity.getQuantity())
                .set(field("price"), productEntity.getPrice())
                .set(field("description"), productEntity.getDescription())
                .set(field("createdAt"), productEntity.getCreatedAt())
                .set(field("lastQuantityModified"), productEntity.getLastQuantityModified())
                .create();
    }

    public static List<ProductEntity> getProductsList(int length) {
        var list = new ArrayList<ProductEntity>();
        for (int i = 0; i < length; i++) {
            list.add(getProductEntityWithoutGeneratedId());
        }
        return list;
    }

    public static List<ProductEntity> getProductsListWithId(int length) {
        var list = new ArrayList<ProductEntity>();
        for (int i = 0; i < length; i++) {
            list.add(getProductEntityWithGeneratedId());
        }
        return list;
    }

    public static List<ProductDetailedResponse> getResponsesList(List<ProductEntity> entities) {
        return entities.stream()
                .map(ProductTestDataFactory::getProductDetailedResponse)
                .toList();
    }

    public static PageGetProductResponse<GetProductResponse> getPageGetProductResponse(
            Pageable pageable, List<ProductEntity> entities
    ) {
        var content = entities.stream()
                .map(ProductTestDataFactory::getGetProductResponse)
                .toList();

        var totalElements = (long) entities.size();
        var totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());
        var currentPage = pageable.getPageNumber();
        var pageSize = pageable.getPageSize();
        var currentPageSize = content.size();

        return PageGetProductResponse.<GetProductResponse>builder()
                .content(content)
                .totalCount(totalElements)
                .totalPages(totalPages)
                .currentPage(currentPage)
                .pageSize(pageSize)
                .currentPageSize(currentPageSize)
                .build();
    }

    public static Specification<ProductEntity> getUnrestrictedProductSpecification() {
        return Specification.unrestricted();
    }

    public static ReserveProductCommandInfo getReserveProductCommandInfo(Map<UUID, BigDecimal> productQuantities) {
        return ReserveProductCommandInfo.builder()
                .productQuantities(productQuantities)
                .build();
    }

    public static ProductReservationResponse getReservationResponseWithEmptyProblemsMap(
            List<ProductEntity> productEntityList,
            Map<UUID, BigDecimal> productQuantities
    ) {
        return ProductReservationResponse.builder()
                .reservedProductsInfoMap(productEntityList.stream().collect(Collectors.toMap(
                        ProductEntity::getId,
                        v -> ReservedProductInfo.builder()
                                .priceAtMoment(v.getPrice())
                                .reservedQuantity(productQuantities.get(v.getId()))
                                .build()
                )))
                .problemsMap(Collections.emptyMap())
                .build();
    }
}
