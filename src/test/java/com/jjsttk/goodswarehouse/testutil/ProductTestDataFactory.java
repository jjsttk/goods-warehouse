package com.jjsttk.goodswarehouse.testutil;

import com.jjsttk.goodswarehouse.controller.request.CreateProductRequest;
import com.jjsttk.goodswarehouse.controller.request.UpdateProductRequest;
import com.jjsttk.goodswarehouse.controller.response.GetProductResponse;
import com.jjsttk.goodswarehouse.controller.response.PageGetProductResponse;
import com.jjsttk.goodswarehouse.enums.PriceCurrency;
import com.jjsttk.goodswarehouse.persistence.entity.ProductEntity;
import com.jjsttk.goodswarehouse.service.product.command.ProductServiceCreateCommand;
import com.jjsttk.goodswarehouse.service.product.command.ProductServiceUpdateCommand;
import com.jjsttk.goodswarehouse.service.product.price.exchange.response.ProductPriceExchangeServiceResponse;
import com.jjsttk.goodswarehouse.service.product.response.BaseProductServiceDto;
import org.instancio.Instancio;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.instancio.Select.field;
import static org.instancio.Select.root;


public class ProductTestDataFactory {


    public static String getStringByLength(int length) {
        return Instancio.of(String.class)
                .generate(root(), gen -> gen.string().length(length))
                .create();
    }

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

    public static UpdateProductRequest getUpdateProductRequest(ProductEntity productEntity) {
        return Instancio.of(UpdateProductRequest.class)
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

    public static ProductServiceCreateCommand getProductCreateCommand(ProductEntity productEntity) {
        return Instancio.of(ProductServiceCreateCommand.class)
                .set(field("name"), productEntity.getName())
                .set(field("article"), productEntity.getArticle())
                .set(field("category"), productEntity.getCategory())
                .set(field("quantity"), productEntity.getQuantity())
                .set(field("price"), productEntity.getPrice())
                .set(field("description"), productEntity.getDescription())
                .create();
    }

    public static ProductServiceUpdateCommand getProductUpdateCommand(ProductEntity productEntity) {
        return Instancio.of(ProductServiceUpdateCommand.class)
                .set(field("name"), productEntity.getName())
                .set(field("article"), productEntity.getArticle())
                .set(field("category"), productEntity.getCategory())
                .set(field("quantity"), productEntity.getQuantity())
                .set(field("price"), productEntity.getPrice())
                .set(field("description"), productEntity.getDescription())
                .create();
    }

    public static BaseProductServiceDto getProductServiceResponse(ProductEntity productEntity) {
        return Instancio.of(BaseProductServiceDto.class)
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
        return List.copyOf(list);
    }

    public static List<BaseProductServiceDto> getServiceResponsesList(List<ProductEntity> entities) {
        return entities.stream()
                .map(ProductTestDataFactory::getProductServiceResponse)
                .toList();
    }

    public static PageGetProductResponse<GetProductResponse> getGetPageProductResponse(
            Pageable pageable, List<ProductEntity> entities
    ) {
        var content = entities.stream()
                .map(ProductTestDataFactory::getGetProductResponse)
                .toList();

        var totalElements = entities.size();
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

    public static ProductPriceExchangeServiceResponse getProductPriceExchangeServiceResponse(
            ProductEntity productEntity
    ) {
        return Instancio.of(ProductPriceExchangeServiceResponse.class)
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
}
