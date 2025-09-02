package com.jjsttk.goodswarehouse.testutil;

import com.jjsttk.goodswarehouse.controller.request.CreateProductRequest;
import com.jjsttk.goodswarehouse.controller.request.UpdateProductRequest;
import com.jjsttk.goodswarehouse.controller.response.GetPageProductResponse;
import com.jjsttk.goodswarehouse.controller.response.GetProductResponse;
import com.jjsttk.goodswarehouse.service.command.ProductCreateCommand;
import com.jjsttk.goodswarehouse.service.command.ProductUpdateCommand;
import com.jjsttk.goodswarehouse.service.response.ProductServiceResponse;
import com.jjsttk.goodswarehouse.persistence.entity.ProductEntity;
import org.instancio.Instancio;
import org.springframework.data.domain.Pageable;

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
                .set(field("createdAt"), productEntity.getCreatedAt())
                .set(field("lastQuantityModified"), productEntity.getLastQuantityModified())
                .create();
    }

    public static ProductCreateCommand getProductCreateCommand(ProductEntity productEntity) {
        return Instancio.of(ProductCreateCommand.class)
                .set(field("name"), productEntity.getName())
                .set(field("article"), productEntity.getArticle())
                .set(field("category"), productEntity.getCategory())
                .set(field("quantity"), productEntity.getQuantity())
                .set(field("price"), productEntity.getPrice())
                .set(field("description"), productEntity.getDescription())
                .create();
    }

    public static ProductUpdateCommand getProductUpdateCommand(ProductEntity productEntity) {
        return Instancio.of(ProductUpdateCommand.class)
                .set(field("name"), productEntity.getName())
                .set(field("article"), productEntity.getArticle())
                .set(field("category"), productEntity.getCategory())
                .set(field("quantity"), productEntity.getQuantity())
                .set(field("price"), productEntity.getPrice())
                .set(field("description"), productEntity.getDescription())
                .create();
    }

    public static ProductServiceResponse getProductServiceResponse(ProductEntity productEntity) {
        return Instancio.of(ProductServiceResponse.class)
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

    public static List<ProductServiceResponse> getServiceResponsesList(List<ProductEntity> entities) {
        return entities.stream()
                .map(ProductTestDataFactory::getProductServiceResponse)
                .toList();
    }

    public static GetPageProductResponse<GetProductResponse> getGetPageProductResponse(
            Pageable pageable, List<ProductEntity> entities
    ) {
        var content = entities.stream()
                .map(ProductTestDataFactory::getGetProductResponse)
                .toList();

        int totalElements = entities.size();
        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());
        int currentPage = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        int currentPageSize = content.size();

        return GetPageProductResponse.<GetProductResponse>builder()
                .content(content)
                .totalCount(totalElements)
                .totalPages(totalPages)
                .currentPage(currentPage)
                .pageSize(pageSize)
                .currentPageSize(currentPageSize)
                .build();
    }
}
