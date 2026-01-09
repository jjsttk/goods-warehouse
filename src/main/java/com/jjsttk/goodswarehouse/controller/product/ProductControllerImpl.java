package com.jjsttk.goodswarehouse.controller.product;

import com.jjsttk.goodswarehouse.controller.product.dto.request.CreateProductRequest;
import com.jjsttk.goodswarehouse.controller.product.dto.request.UpdateProductRequest;
import com.jjsttk.goodswarehouse.controller.product.dto.response.GetProductResponse;
import com.jjsttk.goodswarehouse.controller.product.dto.response.PageGetProductResponse;
import com.jjsttk.goodswarehouse.mapper.product.ProductControllerConverter;
import com.jjsttk.goodswarehouse.service.product.ProductService;
import com.jjsttk.goodswarehouse.service.product.search.advanced.param.AdvancedSearchParam;
import com.jjsttk.goodswarehouse.service.product.search.simple.SimpleSearchDto;
import com.jjsttk.goodswarehouse.service.product.price.exchange.ProductPriceExchangeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@Tag(
        name = "Products",
        description = "CRUD operations for warehouse products"
)
public class ProductControllerImpl implements ProductController {

    private final ProductService productService;
    private final ProductControllerConverter mapper;
    private final ProductPriceExchangeService productExchangeService;

    /**
     * {@inheritDoc}
     */
    @Override
    @GetMapping
    // Pageable defaults: page=0, size=20, sort=productId,asc (defined in interface)
    public PageGetProductResponse<GetProductResponse> getAllProducts(Pageable controllerPageableRequest) {
        var pageBaseProductServiceDto =
                productService.getAll(controllerPageableRequest);
        var pageExchangeServiceResponse =
                productExchangeService.exchange(pageBaseProductServiceDto);

        return mapper.toResponse(pageExchangeServiceResponse);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @GetMapping("/search")
    public PageGetProductResponse<GetProductResponse> search(@Valid SimpleSearchDto simpleSearchDto) {
        var pageBaseProductServiceDto =
                productService.simpleSearch(simpleSearchDto);
        var pageExchangeServiceResponse =
                productExchangeService.exchange(pageBaseProductServiceDto);

        return mapper.toResponse(pageExchangeServiceResponse);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PostMapping("/search")
    // Pageable defaults: page=0, size=20, sort=productId,asc (defined in interface)
    public PageGetProductResponse<GetProductResponse> search(
            Pageable pageable,
            @Valid @RequestBody List<AdvancedSearchParam<?>> advancedSearchParams
    ) {
        var pageBaseProductServiceDto =
                productService.advancedSearch(pageable, advancedSearchParams);
        var pageExchangeServiceResponse =
                productExchangeService.exchange(pageBaseProductServiceDto);

        return mapper.toResponse(pageExchangeServiceResponse);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @GetMapping("/{id}")
    public GetProductResponse getProductById(@PathVariable UUID id) {
        var baseProductServiceDto = productService.getById(id);
        var exchangeServiceResponse = productExchangeService.exchange(baseProductServiceDto);

        return mapper.toResponse(exchangeServiceResponse);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UUID createProduct(@Valid @RequestBody CreateProductRequest createProductRequest) {
        var productServiceCreateCommand = mapper.toCommand(createProductRequest);
        var baseProductServiceDto = productService.create(productServiceCreateCommand);
        return baseProductServiceDto.id();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PatchMapping("/{id}")
    public UUID updateProductById(@PathVariable UUID id,
                                  @Valid @RequestBody UpdateProductRequest updateDto) {
        var productServiceUpdateCommand = mapper.toCommand(updateDto);
        var baseProductServiceDto = productService.update(id, productServiceUpdateCommand);
        return baseProductServiceDto.id();
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
