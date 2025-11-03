package com.jjsttk.goodswarehouse.service.product;

import com.jjsttk.goodswarehouse.service.product.response.BaseProductServiceDto;
import com.jjsttk.goodswarehouse.service.product.search.advanced.param.AdvancedSearchParam;
import com.jjsttk.goodswarehouse.service.product.search.simple.SimpleSearchDto;
import com.jjsttk.goodswarehouse.exception.service.ResourceNotFoundException;
import com.jjsttk.goodswarehouse.service.product.command.ProductServiceCreateCommand;
import com.jjsttk.goodswarehouse.service.product.command.ProductServiceUpdateCommand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    /**
     * Retrieves a paginated list of products from the database.
     *
     * @param pageable pagination and sorting information
     * @return a page of {@link BaseProductServiceDto} representing the products
     */
    Page<BaseProductServiceDto> getAll(Pageable pageable);

    /**
     * Retrieves a product by its unique identifier.
     *
     * @param id the UUID of the product
     * @return product service response representation of the found product
     * @throws ResourceNotFoundException if no product is found with the given id
     */
    BaseProductServiceDto getById(UUID id);

    /**
     * Creates a new product in the database.
     *
     * @param createCommandDto request DTO containing product creation data
     * @return product service response containing the ID of the created product
     */
    BaseProductServiceDto create(ProductServiceCreateCommand createCommandDto);

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
    BaseProductServiceDto update(ProductServiceUpdateCommand updateCommandDto, UUID id);

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
     * @return a page of {@link BaseProductServiceDto} representing the products
     */
    Page<BaseProductServiceDto> simpleSearch(SimpleSearchDto simpleSearchDto);

    /**
     * Retrieves a paginated list of products from the database.
     * @param pageable pagination and sorting information
     * @param filterParams filter parameters and page information
     * @return a page of {@link BaseProductServiceDto} representing the products
     */
    Page<BaseProductServiceDto> advancedSearch(Pageable pageable, List<AdvancedSearchParam<?>> filterParams);
}
