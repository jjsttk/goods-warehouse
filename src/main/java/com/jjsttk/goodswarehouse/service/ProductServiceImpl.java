package com.jjsttk.goodswarehouse.service;

import com.jjsttk.goodswarehouse.exception.NotUniqueArticleException;
import com.jjsttk.goodswarehouse.exception.ResourceNotFoundException;
import com.jjsttk.goodswarehouse.mapper.ProductConverter;
import com.jjsttk.goodswarehouse.persistence.entity.ProductEntity;
import com.jjsttk.goodswarehouse.persistence.repository.ProductRepository;
import com.jjsttk.goodswarehouse.service.command.ProductCreateCommand;
import com.jjsttk.goodswarehouse.service.command.ProductUpdateCommand;
import com.jjsttk.goodswarehouse.service.response.ProductServiceResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
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
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public ProductServiceResponse create(ProductCreateCommand createCommandDto) {
        checkArticleUnique(createCommandDto.getArticle());
        var entity = productConverter.mapToEntity(createCommandDto);
        entity.setLastQuantityModified(OffsetDateTime.now());
        productRepository.save(entity);

        return productConverter.mapToServiceResponse(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public ProductServiceResponse update(ProductUpdateCommand updateCommandDto, UUID id) {
        if (updateCommandDto.getArticle() != null) {
            checkArticleUnique(updateCommandDto.getArticle(), id);
        }

        var entity = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
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
        var productEntity = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));

        productRepository.delete(productEntity);
    }

    private void checkArticleUnique(String article) {
        var mbProduct = productRepository.findByArticle(article);
        if (mbProduct.isPresent()) {
            throw new NotUniqueArticleException(mbProduct.get().getId());
        }
    }

    private void checkArticleUnique(String article, UUID id) {
        var mbProduct = productRepository.findByArticle(article);
        if (mbProduct.isPresent() && !mbProduct.get().getId().equals(id)) {
            throw new NotUniqueArticleException(mbProduct.get().getId());
        }
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
