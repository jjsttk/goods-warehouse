package com.jjsttk.goodswarehouse.service;

import com.jjsttk.goodswarehouse.service.search.advanced.param.AdvancedSearchParam;
import com.jjsttk.goodswarehouse.service.search.simple.SimpleSearchDto;
import com.jjsttk.goodswarehouse.exception.ResourceNotFoundException;
import com.jjsttk.goodswarehouse.service.command.ProductCreateCommand;
import com.jjsttk.goodswarehouse.service.command.ProductUpdateCommand;
import com.jjsttk.goodswarehouse.service.response.ProductServiceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    /**
     * Retrieves a paginated list of products from the database.
     *
     * @param pageable pagination and sorting information
     * @return a page of {@link ProductServiceResponse} representing the products
     */
    Page<ProductServiceResponse> getAll(Pageable pageable);

    /**
     * Retrieves a product by its unique identifier.
     *
     * @param id the UUID of the product
     * @return product service response representation of the found product
     * @throws ResourceNotFoundException if no product is found with the given id
     */
    ProductServiceResponse getById(UUID id);

    /**
     * Creates a new product in the database.
     *
     * @param createCommandDto request DTO containing product creation data
     * @return product service response containing the ID of the created product
     */
    ProductServiceResponse create(ProductCreateCommand createCommandDto);

    /**
     * Updates an existing product with new values.
     * <p>
     * Only non-null fields from the DTO will be updated. Ensures
     * article uniqueness, category validity, and correct constraints.
     * </p>
     *
     * @param updateCommandDto DTO containing updated fields
     * @param id               UUID of the product to update
     * @return updated product service response
     * @throws ResourceNotFoundException if the product is not found
     */
    ProductServiceResponse update(ProductUpdateCommand updateCommandDto, UUID id);

    /**
     * Deletes a product by its UUID.
     *
     * @param id UUID of the product to delete
     * @throws ResourceNotFoundException if no product exists with the given id
     */
    void delete(UUID id);

    /**
     * Retrieves a paginated list of products from the database.
     *
     * @param simpleSearchDto filter parameters and page information
     * @return a page of {@link ProductServiceResponse} representing the products
     */
    Page<ProductServiceResponse> simpleSearch(SimpleSearchDto simpleSearchDto);

    /**
     * Retrieves a paginated list of products from the database.
     * @param pageable pagination and sorting information
     * @param filterParams filter parameters and page information
     * @return a page of {@link ProductServiceResponse} representing the products
     */
    Page<ProductServiceResponse> advancedSearch(Pageable pageable, List<AdvancedSearchParam<?>> filterParams);
}
