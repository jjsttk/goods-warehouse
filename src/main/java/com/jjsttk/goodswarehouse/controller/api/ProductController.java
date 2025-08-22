package com.jjsttk.goodswarehouse.controller.api;

import com.jjsttk.goodswarehouse.dto.request.ProductRequestCreateDto;
import com.jjsttk.goodswarehouse.dto.request.ProductRequestUpdateDto;
import com.jjsttk.goodswarehouse.dto.response.ProductResponseDto;
import com.jjsttk.goodswarehouse.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing products in the warehouse.
 */
@RestController
@RequestMapping("/api/v1/products")
@AllArgsConstructor
@Tag(name = "Products", description = "CRUD operations for warehouse products")
public class ProductController {

    private final ProductService productService;

    /**
     * Get all products.
     *
     * @return list of {@link ProductResponseDto}
     */
    @Operation(
            summary = "Get all products",
            description = "Returns a list of all available products",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List retrieved successfully",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = ProductResponseDto.class))))
            }
    )
    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> index() {
        return ResponseEntity.ok(productService.getAll());
    }

    /**
     * Get product by ID.
     *
     * @param id UUID of the product
     * @return {@link ProductResponseDto}
     */
    @Operation(
            summary = "Get product by ID",
            description = "Returns a single product by its UUID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ProductResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Product not found",
                            content = @Content(mediaType = "application/json"))
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> show(
            @Parameter(description = "UUID of the product") @PathVariable UUID id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    /**
     * Create a new product.
     *
     * @param createDto request body with product details
     * @return created {@link ProductResponseDto}
     */
    @Operation(
            summary = "Create a new product",
            description = "Creates and stores a new product in the warehouse",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Product created successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ProductResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data",
                            content = @Content(mediaType = "application/json"))
            }
    )
    @PostMapping
    public ResponseEntity<ProductResponseDto> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Product data to create",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ProductRequestCreateDto.class))
            )
            @Valid @RequestBody ProductRequestCreateDto createDto) {
        var productResponseDto = productService.create(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(productResponseDto);
    }

    /**
     * Update an existing product.
     *
     * @param id        UUID of the product
     * @param updateDto request body with updated fields
     * @return updated {@link ProductResponseDto}
     */
    @Operation(
            summary = "Update an existing product",
            description = "Updates product details by its UUID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product updated successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ProductResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Product not found",
                            content = @Content(mediaType = "application/json"))
            }
    )
    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponseDto> update(
            @Parameter(description = "UUID of the product") @PathVariable UUID id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Product data to update",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ProductRequestUpdateDto.class))
            )
            @Valid @RequestBody ProductRequestUpdateDto updateDto) {
        return ResponseEntity.ok(productService.update(updateDto, id));
    }

    /**
     * Delete product by ID.
     *
     * @param id UUID of the product
     * @return HTTP 204 if deletion is successful
     */
    @Operation(
            summary = "Delete a product",
            description = "Removes a product by its UUID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Product deleted successfully",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "Product not found",
                            content = @Content(mediaType = "application/json"))
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "UUID of the product") @PathVariable UUID id) {
        productService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
