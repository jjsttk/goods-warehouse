package com.jjsttk.goodswarehouse.service;

import com.jjsttk.goodswarehouse.dto.request.ProductRequestCreateDto;
import com.jjsttk.goodswarehouse.dto.request.ProductRequestUpdateDto;
import com.jjsttk.goodswarehouse.dto.response.ProductResponseDto;
import com.jjsttk.goodswarehouse.exception.ResourceNotFoundException;
import com.jjsttk.goodswarehouse.mapper.ProductConverter;
import com.jjsttk.goodswarehouse.model.entity.Product;
import com.jjsttk.goodswarehouse.model.enums.Category;
import com.jjsttk.goodswarehouse.repository.ProductRepository;
import com.jjsttk.goodswarehouse.utils.DtoNormalizer;
import com.jjsttk.goodswarehouse.utils.ExceptionMessage;
import com.jjsttk.goodswarehouse.utils.ValidationMessage;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Service class for managing {@link Product} entities.
 * <p>
 * Provides business logic for CRUD operations with products,
 * including validation, normalization of DTOs, and mapping
 * between entities and response objects.
 * </p>
 *
 * <p>All modifying operations are wrapped with {@link org.springframework.transaction.annotation.Transactional}
 * to ensure data consistency in concurrent environments.</p>
 */
@Service
@AllArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductConverter productConverter;

    /**
     * Retrieves all products from the database.
     *
     * @return list of {@link ProductResponseDto} objects representing all products
     */
    public List<ProductResponseDto> getAll() {
        return productRepository.findAll().stream()
                .map(productConverter::mapToDto)
                .toList();
    }

    /**
     * Retrieves a product by its unique identifier.
     *
     * @param id the UUID of the product
     * @return {@link ProductResponseDto} representation of the found product
     * @throws ResourceNotFoundException if no product is found with the given id
     */
    public ProductResponseDto getById(UUID id) {
        return productRepository.findById(id)
                .map(productConverter::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionMessage.entityNotFoundMessage(Product.class, id)
                ));
    }

    /**
     * Creates a new product in the database.
     * <p>
     * Performs validation of article uniqueness and category correctness.
     * </p>
     *
     * @param createDto request DTO containing product creation data
     * @return {@link ProductResponseDto} of the created product
     * @throws ValidationException if the article is not unique or category is invalid
     */
    @Transactional
    public ProductResponseDto create(ProductRequestCreateDto createDto) {
        checkArticleUnique(createDto.getArticle());
        DtoNormalizer.normalize(createDto);
        validateCategory(createDto.getCategory());

        Product entity = productConverter.mapToEntity(createDto);
        Product saved = productRepository.save(entity);

        return productConverter.mapToDto(saved);
    }

    /**
     * Updates an existing product with new values.
     * <p>
     * Only non-null fields from the DTO will be updated. Ensures
     * article uniqueness, category validity, and correct constraints.
     * </p>
     *
     * @param updateDto DTO containing updated fields
     * @param id        UUID of the product to update
     * @return updated {@link ProductResponseDto}
     * @throws ResourceNotFoundException if the product is not found
     * @throws ValidationException       if validation fails (e.g. duplicate article, invalid price)
     */
    @Transactional
    public ProductResponseDto update(ProductRequestUpdateDto updateDto, UUID id) {
        Product entity = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionMessage.entityNotFoundMessage(Product.class, id)
                ));

        DtoNormalizer.normalize(updateDto);

        updateName(updateDto, entity);
        updateDescription(updateDto, entity);
        updateCategory(updateDto, entity);
        updateArticle(updateDto, entity);
        updatePrice(updateDto, entity);
        updateQuantity(updateDto, entity);

        productRepository.save(entity);

        return productConverter.mapToDto(entity);
    }

    /**
     * Deletes a product by its UUID.
     *
     * @param id UUID of the product to delete
     * @throws ResourceNotFoundException if no product exists with the given id
     */
    @Transactional
    public void delete(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionMessage.entityNotFoundMessage(Product.class, id)));
        productRepository.delete(product);
    }


    /**
     * Ensures that the product article is unique.
     *
     * @param article product article number
     * @throws ValidationException if article already exists
     */
    private void checkArticleUnique(Long article) {
        productRepository.findByArticle(article)
                .ifPresent(p -> {
                    throw new ValidationException(
                            ValidationMessage.entityWithThisArticleExistsMessage(Product.class, article)
                    );
                });
    }

    /**
     * Validates if category string corresponds to an existing {@link Category}.
     *
     * @param categoryStr category string
     * @throws ValidationException if category is invalid
     */
    private void validateCategory(String categoryStr) {
        try {
            Category.valueOf(categoryStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException(
                    ValidationMessage.categoryDoesNotExistsMessage(Category.class, categoryStr)
            );
        }
    }

    // ---------------- Private Update Helpers ---------------- //

    private void updateName(ProductRequestUpdateDto dto, Product entity) {
        Optional.ofNullable(dto.getName())
                .ifPresent(name -> {
                    if (name.isBlank()) {
                        throw new ValidationException(
                                ValidationMessage.nameCannotBeBlankMessage());
                    }
                    entity.setName(name);
                });
    }

    private void updateDescription(ProductRequestUpdateDto dto, Product entity) {
        Optional.ofNullable(dto.getDescription())
                .ifPresent(desc -> {
                    if (desc.isBlank()) {
                        throw new ValidationException(
                                ValidationMessage.descriptionCannotBeBlankMessage());
                    }
                    entity.setDescription(desc);
                });
    }

    private void updateCategory(ProductRequestUpdateDto dto, Product entity) {
        Optional.ofNullable(dto.getCategory())
                .ifPresent(category -> {
                    if (category.isBlank()) {
                        throw new ValidationException(
                                ValidationMessage.categoryCannotBeBlankMessage());
                    }
                    try {
                        entity.setCategory(Category.valueOf(category.toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        throw new ValidationException(
                                ValidationMessage.categoryDoesNotExistsMessage(Category.class, category));
                    }
                });
    }

    private void updateArticle(ProductRequestUpdateDto dto, Product entity) {
        Optional.ofNullable(dto.getArticle())
                .ifPresent(article -> {
                    if (article < 0) {
                        throw new ValidationException(
                                ValidationMessage.articleIsNotValidMessage());
                    }

                    boolean exists = productRepository.existsByArticleAndIdNot(article, entity.getId());
                    if (exists) {
                        throw new ValidationException(
                                ValidationMessage.notUniqueArticleMessage());
                    }

                    entity.setArticle(article);
                });
    }

    private void updatePrice(ProductRequestUpdateDto dto, Product entity) {
        Optional.ofNullable(dto.getPrice())
                .ifPresent(price -> {
                    if (price.compareTo(BigDecimal.ZERO) <= 0) {
                        throw new ValidationException(
                                ValidationMessage.priceIsNotValidMessage());
                    }
                    entity.setPrice(price);
                });
    }

    private void updateQuantity(ProductRequestUpdateDto dto, Product entity) {
        Optional.ofNullable(dto.getQuantity())
                .ifPresent(quantity -> {
                    if (quantity < 0) {
                        throw new ValidationException(
                                ValidationMessage.quantityIsNotValidMessage());
                    }

                    if (!Objects.equals(entity.getQuantity(), quantity)) {
                        entity.setQuantity(quantity);
                        entity.setLastQuantityModified(LocalDateTime.now());
                    }
                });
    }
}
