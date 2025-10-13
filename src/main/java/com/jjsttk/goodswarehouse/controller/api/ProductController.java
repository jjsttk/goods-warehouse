package com.jjsttk.goodswarehouse.controller.api;

import com.jjsttk.goodswarehouse.controller.request.CreateProductRequest;
import com.jjsttk.goodswarehouse.controller.request.UpdateProductRequest;
import com.jjsttk.goodswarehouse.controller.response.GetPageProductResponse;
import com.jjsttk.goodswarehouse.controller.response.GetProductResponse;
import com.jjsttk.goodswarehouse.exception.ErrorResponse;
import com.jjsttk.goodswarehouse.service.search.advanced.param.AdvancedSearchParam;
import com.jjsttk.goodswarehouse.service.search.advanced.param.BigDecimalParam;
import com.jjsttk.goodswarehouse.service.search.advanced.param.LocalDateParam;
import com.jjsttk.goodswarehouse.service.search.simple.SimpleSearchDto;
import com.jjsttk.goodswarehouse.service.search.advanced.param.StringParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

/**
 * REST API interface for managing warehouse products.
 * Provides endpoints for product CRUD operations with validation and pagination support.
 * Time format: All timestamps are returned in ISO-8601 format with UTC offset
 * (e.g., 2023-10-05T12:00:00+00:00). Clients can use the provided offset
 * for accurate timezone conversion without additional calculations.
 */
@Tag(
        name = "Product Management",
        description = "APIs for managing products in the warehouse"
)
public interface ProductController {

    /**
     * Retrieves a paginated list of products from the warehouse.
     * Uses Spring Data's 0-based pagination (page 0 = first page).
     *
     * @param pageableRequest pagination parameters (page number starts from 0 in the response), size, sorting
     * @return paginated response with product list and metadata
     */
    @Operation(
            summary = "Get paginated products",
            description = "Returns a paginated list of products with metadata including totalCount. "
                    + "All timestamps include timezone offset information. "
                    + "Supports sorting by any product field and custom page size. "
                    + "Uses standard Spring Data 0-based pagination.",
            parameters = {
                    @Parameter(name = "page", description = "Page number (0-based)", example = "0"),
                    @Parameter(name = "size", description = "Number of items per page", example = "20"),
                    @Parameter(name = "sort", description = "Sorting criteria in the format: property(,asc|desc). "
                            + "Default sort order is ascending. Multiple sort criteria are supported.",
                            example = "name,asc")
            },
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Paginated products retrieved successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = GetPageProductResponse.class))),
                    @ApiResponse(responseCode = "400",
                            description = "Invalid pagination parameters",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500",
                            description = "Internal server error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    GetPageProductResponse<GetProductResponse> getAllProducts(
            @Parameter(description = "Pagination and sorting parameters. Example: ?page=1&size=10&sort=name,asc",
                    hidden = true)
            @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageableRequest
    );


    /**
     * Searches for products using multiple filter parameters.
     * Supports filtering by name, price range, quantity range and pagination.
     *
     * @param searchParams filter parameters including optional name, price max,
     *                     quantity and required size and page for page request settings
     * @return paginated list of filtered products with metadata
     */
    @Operation(
            summary = "Search products with filters",
            description = "Performs a filtered search for products based on provided criteria. "
                    + "Supports filtering by name (partial match), price range, quantity range, "
                    + "and pagination with sorting. Returns a paginated result with metadata.",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Filtered products retrieved successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = GetPageProductResponse.class))),
                    @ApiResponse(responseCode = "400",
                            description = "Invalid filter parameters",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500",
                            description = "Internal server error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    GetPageProductResponse<GetProductResponse> search(
            @Parameter(
                    description = "Filter parameters for searching products. "
                            + "Only non-null fields will be applied as filters.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = SimpleSearchDto.class))
            )
            @Valid SimpleSearchDto searchParams
    );

    /**
     * Performs an advanced product search using a list of filter parameters.
     * Each filter corresponds to a specific field, comparison operation,
     * and value. Only non-null values are applied.
     * <p>
     * Supports combining multiple conditions and paginating the result.
     * Uses Spring Data's 0-based pagination (page 0 = first page).
     *
     * @param pageable             pagination and sorting parameters
     * @param advancedSearchParams list of filter parameters with field name,
     *                             operation type and value
     * @return paginated list of filtered products with metadata
     */
    @Operation(
            summary = "Advanced search with multiple filters",
            description = "Allows searching products by combining multiple filter parameters. "
                    + "Each parameter contains a target field, "
                    + "comparison operation (e.g., EQUAL, GREATER_THAN_OR_EQUAL), "
                    + "and a value. Supports numeric, date, and string filtering. "
                    + "Results are returned in a paginated format with metadata.",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Filtered products retrieved successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = GetPageProductResponse.class))),
                    @ApiResponse(responseCode = "400",
                            description = "Invalid filter parameters",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500",
                            description = "Internal server error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    GetPageProductResponse<GetProductResponse> search(
            @Parameter(description = "Pagination and sorting parameters. Example: ?page=1&size=10&sort=name,asc",
                    hidden = true)
            @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable,
            @Parameter(
                    description = "List of advanced search conditions",
                    content = @Content(
                            schema = @Schema(oneOf = {
                                    StringParam.class,
                                    BigDecimalParam.class,
                                    LocalDateParam.class
                            })
                    )
            )
            @Valid @RequestBody List<AdvancedSearchParam<?>> advancedSearchParams
    );

    /**
     * Retrieves a product by its unique ID.
     *
     * @param id UUID of the product
     * @return product details
     */
    @Operation(
            summary = "Get product by ID",
            description = "Returns detailed information about a product using its unique identifier.",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Product retrieved successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = GetProductResponse.class))),
                    @ApiResponse(responseCode = "404",
                            description = "Product not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500",
                            description = "Internal server error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    GetProductResponse getProductById(
            @Parameter(
                    description = "UUID of the product", required = true,
                    example = "123e4567-e89b-12d3-a456-426614174000"
            )
            @PathVariable UUID id
    );

    /**
     * Creates a new product in the warehouse.
     *
     * @param createProductRequest product creation data
     * @return UUID of the created product
     */
    @Operation(
            summary = "Create a new product",
            description = "Adds a new product to the warehouse. Returns the UUID of the created product.",
            responses = {
                    @ApiResponse(responseCode = "201",
                            description = "Product created successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UUID.class))),
                    @ApiResponse(responseCode = "400",
                            description = "Validation failed or invalid input",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "422",
                            description = "Business rule conflict or data integrity violation",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500",
                            description = "Internal server error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    UUID createProduct(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Product creation payload",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateProductRequest.class))
            )
            @Valid @RequestBody CreateProductRequest createProductRequest
    );

    /**
     * Updates an existing product.
     *
     * @param id        UUID of the product
     * @param updateDto updated product data
     * @return UUID of the updated product
     */
    @Operation(
            summary = "Update an existing product",
            description = "Updates details of an existing product and returns the UUID of the updated product.",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Product updated successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UUID.class))),
                    @ApiResponse(responseCode = "400",
                            description = "Validation failed or invalid input",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404",
                            description = "Product not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "422",
                            description = "Business rule conflict or data integrity violation",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500",
                            description = "Internal server error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    UUID updateProductById(
            @Parameter(description = "UUID of the product to update", required = true,
                    example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Product update payload",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateProductRequest.class))
            )
            @Valid @RequestBody UpdateProductRequest updateDto
    );

    /**
     * Deletes a product by UUID.
     *
     * @param id UUID of the product
     */
    @Operation(
            summary = "Delete a product",
            description = "Deletes a product from the warehouse using its unique identifier.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
                    @ApiResponse(responseCode = "404",
                            description = "Product not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500",
                            description = "Internal server error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    void deleteProductById(
            @Parameter(description = "UUID of the product to delete", required = true,
                    example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id
    );
}
