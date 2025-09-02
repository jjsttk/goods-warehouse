package com.jjsttk.goodswarehouse.service;

import com.jjsttk.goodswarehouse.exception.FieldValidationException;
import com.jjsttk.goodswarehouse.service.command.ProductCreateCommand;
import com.jjsttk.goodswarehouse.service.command.ProductUpdateCommand;
import com.jjsttk.goodswarehouse.service.response.ProductServiceResponse;
import com.jjsttk.goodswarehouse.exception.ResourceNotFoundException;
import com.jjsttk.goodswarehouse.mapper.ProductConverter;
import com.jjsttk.goodswarehouse.persistence.entity.ProductEntity;
import com.jjsttk.goodswarehouse.persistence.repository.ProductRepository;
import com.jjsttk.goodswarehouse.utils.ExceptionMessage;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Service class for managing {@link ProductEntity} entities.
 * <p>
 * Provides business logic for CRUD operations with products,
 * including validation, normalization of DTOs, and mapping
 * between entities and response objects.
 * </p>
 *
 * <p>All modifying operations are wrapped with {@link Transactional}
 * to ensure data consistency in concurrent environments.</p>
 */
@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductConverter productConverter;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ProductServiceResponse> getAll(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productConverter::mapToServiceResponse);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public ProductServiceResponse getById(UUID id) {
        return productRepository.findById(id)
                .map(productConverter::mapToServiceResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionMessage.entityNotFoundMessage(ProductEntity.class, id)
                ));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public ProductServiceResponse create(ProductCreateCommand createCommandDto) {
        ProductEntity entity = productConverter.mapToEntity(createCommandDto);
        ProductEntity saved = productRepository.save(entity);
        return productConverter.mapToServiceResponse(saved);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public ProductServiceResponse update(ProductUpdateCommand updateCommandDto, UUID id) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionMessage.entityNotFoundMessage(ProductEntity.class, id)
                ));

        var mbErrors = validateUpdate(updateCommandDto, id);

        if (!mbErrors.isEmpty()) {
            throw new FieldValidationException(mbErrors);
        }

        updateProductEntity(updateCommandDto, entity);
        productRepository.save(entity);

        return productConverter.mapToServiceResponse(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void delete(UUID id) {
        ProductEntity productEntity = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionMessage.entityNotFoundMessage(ProductEntity.class, id)));
        productRepository.delete(productEntity);
    }



    // ---------------- Private Validate Helpers ---------------- //

    private Map<String, List<String>> validateUpdate(ProductUpdateCommand command, UUID id) {
        Map<String, List<String>> errors = new HashMap<>();

        validateName(command, errors);
        validateArticle(command, id, errors);
        validatePrice(command, errors);
        validateQuantity(command, errors);

        return errors;
    }


    private static void validateQuantity(ProductUpdateCommand command, Map<String, List<String>> errors) {
        Optional.ofNullable(command.getQuantity())
                .ifPresent(quantity -> {
                    if (quantity.signum() < 0) {
                        errors.computeIfAbsent("quantity", k -> new ArrayList<>())
                                .add("Quantity must be positive or zero");
                    }

                    var ruleMaxIntegerDigits = 9;
                    var ruleMaxScale = 3;
                    if ((quantity.precision() - quantity.scale()) > ruleMaxIntegerDigits
                            || (quantity.scale() > ruleMaxScale)) {
                        errors.computeIfAbsent("quantity", k -> new ArrayList<>())
                                .add("Quantity must have up to 9 digits before decimal and 3 after");
                    }
                });
    }

    private static void validatePrice(ProductUpdateCommand command, Map<String, List<String>> errors) {
        Optional.ofNullable(command.getPrice())
                .ifPresent(price -> {
                    if (price.signum() <= 0) {
                        errors.computeIfAbsent("price", k -> new ArrayList<>())
                                .add("Price must be positive");
                    }

                    var ruleMaxIntegerDigits = 8;
                    var ruleMaxScale = 2;
                    if (price.precision() - price.scale() > ruleMaxIntegerDigits || price.scale() > ruleMaxScale) {
                        errors.computeIfAbsent("price", k -> new ArrayList<>())
                                .add("Price must have up to 8 digits before decimal and 2 after");
                    }
                });
    }

    private void validateArticle(ProductUpdateCommand command, UUID id, Map<String, List<String>> errors) {
        Optional.ofNullable(command.getArticle())
                .ifPresent(article -> {
                    String stripped = article.strip();
                    if (stripped.isEmpty()) {
                        errors.computeIfAbsent("article", k -> new ArrayList<>())
                                .add("Article must not be blank");
                    }
                    if (stripped.length() > 100) {
                        errors.computeIfAbsent("article", k -> new ArrayList<>())
                                .add("Article must be no longer than 100 characters");
                    }
                    productRepository.findByArticle(stripped)
                            .ifPresent(existingProduct -> {
                                if (!existingProduct.getId().equals(id)) {
                                    errors.computeIfAbsent("article", k -> new ArrayList<>())
                                            .add(ExceptionMessage.entityWithThisIdAlreadyUsesThisArticleMessage(
                                                    ProductEntity.class, id
                                            ));
                                }
                            });
                });
    }

    private static void validateName(ProductUpdateCommand command, Map<String, List<String>> errors) {
        Optional.ofNullable(command.getName())
                .ifPresent(name -> {
                    String stripped = name.strip();
                    if (stripped.isEmpty()) {
                        errors.computeIfAbsent("name", k -> new ArrayList<>())
                                .add("Name must not be blank");
                    } else if (stripped.length() > 50) {
                        errors.computeIfAbsent("name", k -> new ArrayList<>())
                                .add("Name must be no longer than 50 characters");
                    }
                });
    }

    // ---------------- Private Update Helpers ---------------- //

    private void updateProductEntity(ProductUpdateCommand updateCommandDto, ProductEntity entity) {
        updateName(updateCommandDto, entity);
        updateDescription(updateCommandDto, entity);
        updateCategory(updateCommandDto, entity);
        updateArticle(updateCommandDto, entity);
        updatePrice(updateCommandDto, entity);
        updateQuantity(updateCommandDto, entity);
    }

    private void updateName(ProductUpdateCommand command, ProductEntity entity) {
        Optional.ofNullable(command.getName())
                .map(String::strip)
                .ifPresent(entity::setName);
    }

    private void updateDescription(ProductUpdateCommand command, ProductEntity entity) {
        Optional.ofNullable(command.getDescription())
                .map(String::strip)
                .ifPresent(entity::setDescription);
    }

    private void updateCategory(ProductUpdateCommand command, ProductEntity entity) {
        Optional.ofNullable(command.getCategory())
                .ifPresent(entity::setCategory);
    }

    private void updateArticle(ProductUpdateCommand command, ProductEntity entity) {
        Optional.ofNullable(command.getArticle())
                .map(String::strip)
                .ifPresent(entity::setArticle);
    }

    private void updatePrice(ProductUpdateCommand command, ProductEntity entity) {
        Optional.ofNullable(command.getPrice())
                .ifPresent(entity::setPrice);
    }

    private void updateQuantity(ProductUpdateCommand command, ProductEntity entity) {
        Optional.ofNullable(command.getQuantity())
                .ifPresent(quantity -> {
                    if (!Objects.equals(entity.getQuantity(), quantity)) {
                        entity.setQuantity(quantity);
                        entity.setLastQuantityModified(OffsetDateTime.now());
                    }
                });
    }
}
