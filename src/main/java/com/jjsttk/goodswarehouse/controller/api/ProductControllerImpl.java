package com.jjsttk.goodswarehouse.controller.api;

import com.jjsttk.goodswarehouse.controller.request.CreateProductRequest;
import com.jjsttk.goodswarehouse.service.search.advanced.param.AdvancedSearchParam;
import com.jjsttk.goodswarehouse.service.search.simple.SimpleSearchDto;
import com.jjsttk.goodswarehouse.controller.request.UpdateProductRequest;
import com.jjsttk.goodswarehouse.controller.response.GetPageProductResponse;
import com.jjsttk.goodswarehouse.controller.response.GetProductResponse;
import com.jjsttk.goodswarehouse.mapper.ProductConverter;
import com.jjsttk.goodswarehouse.service.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing products in the warehouse.
 * Provides CRUD operations for products including creation, retrieval, update, and deletion.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/products")
@AllArgsConstructor
@Tag(
        name = "Products",
        description = "CRUD operations for warehouse products"
)
public class ProductControllerImpl implements ProductController {

    private final ProductService productService;
    private final ProductConverter mapper;

    /**
     * {@inheritDoc}
     */
    @Override
    @GetMapping
    // Pageable defaults: page=0, size=20, sort=id,asc (defined in interface)
    public GetPageProductResponse<GetProductResponse> getAllProducts(Pageable controllerPageableRequest) {
        var serviceResponse = productService.getAll(controllerPageableRequest);
        return mapper.mapToControllerResponse(serviceResponse);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @GetMapping("/search")
    public GetPageProductResponse<GetProductResponse> search(@Valid SimpleSearchDto simpleSearchDto) {
        var serviceResponse = productService.simpleSearch(simpleSearchDto);
        return mapper.mapToControllerResponse(serviceResponse);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PostMapping("/search")
    // Pageable defaults: page=0, size=20, sort=id,asc (defined in interface)
    public GetPageProductResponse<GetProductResponse> search(
            Pageable pageable,
            @Valid @RequestBody List<AdvancedSearchParam<?>> advancedSearchParams
    ) {
        var serviceResponse = productService.advancedSearch(pageable, advancedSearchParams);
        return mapper.mapToControllerResponse(serviceResponse);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @GetMapping("/{id}")
    public GetProductResponse getProductById(@PathVariable UUID id) {
        var serviceResponse = productService.getById(id);
        return mapper.mapToControllerResponse(serviceResponse);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UUID createProduct(@Valid @RequestBody CreateProductRequest createProductRequest) {
        var productCreateCommand = mapper.mapToServiceCommand(createProductRequest);
        var serviceResponse = productService.create(productCreateCommand);
        return serviceResponse.id();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PatchMapping("/{id}")
    public UUID updateProductById(@PathVariable UUID id,
                                  @Valid @RequestBody UpdateProductRequest updateDto) {
        var updateCommand = mapper.mapToServiceCommand(updateDto);
        var serviceResponse = productService.update(updateCommand, id);
        return serviceResponse.id();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProductById(@PathVariable UUID id) {
        productService.delete(id);
    }
}
