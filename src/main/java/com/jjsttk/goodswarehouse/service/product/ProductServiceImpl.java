package com.jjsttk.goodswarehouse.service.product;

import com.jjsttk.goodswarehouse.exception.service.ResourceNotFoundException;
import com.jjsttk.goodswarehouse.exception.service.order.NotEnoughQuantityInStockException;
import com.jjsttk.goodswarehouse.exception.service.order.product.ProductsToOrderNotFoundException;
import com.jjsttk.goodswarehouse.exception.service.product.NotUniqueArticleException;
import com.jjsttk.goodswarehouse.mapper.product.ProductServiceConverter;
import com.jjsttk.goodswarehouse.persistence.entity.product.ProductEntity;
import com.jjsttk.goodswarehouse.persistence.repository.ProductRepository;
import com.jjsttk.goodswarehouse.service.product.dto.command.ProductServiceCreateCommand;
import com.jjsttk.goodswarehouse.service.product.dto.command.ProductServiceReservationCommand;
import com.jjsttk.goodswarehouse.service.product.dto.command.ProductServiceUpdateCommand;
import com.jjsttk.goodswarehouse.service.product.dto.response.ProductServiceProductDetailedResponse;
import com.jjsttk.goodswarehouse.service.product.dto.response.ProductServiceReservationResponse;
import com.jjsttk.goodswarehouse.service.product.dto.response.ProductServiceReservedProductInfo;
import com.jjsttk.goodswarehouse.service.product.search.advanced.param.AdvancedSearchParam;
import com.jjsttk.goodswarehouse.service.product.search.simple.SimpleSearchDto;
import com.jjsttk.goodswarehouse.service.product.search.specification.ProductSpecification;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private final ProductServiceConverter mapper;
    private final ProductSpecification productSpecification;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ProductServiceProductDetailedResponse> getAll(Pageable pageable) {
        return productRepository.findAll(pageable).map(mapper::toResponse);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public ProductServiceProductDetailedResponse getById(UUID id) {
        return productRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(ProductEntity.class, id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public ProductServiceProductDetailedResponse create(ProductServiceCreateCommand createCommandDto) {
        checkArticleUnique(createCommandDto.article());
        var entity = mapper.toEntity(createCommandDto);
        productRepository.save(entity);

        return mapper.toResponse(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public ProductServiceProductDetailedResponse update(UUID productId, ProductServiceUpdateCommand updateCommandDto) {
        var product = getProductForUpdate(productId);
        validateArticleUniquenessIfChanged(product, updateCommandDto.article());

        mapper.update(product, updateCommandDto);

        return mapper.toResponse(product);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void delete(UUID id) {
        var productEntity = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ProductEntity.class, id));

        productRepository.delete(productEntity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Page<ProductServiceProductDetailedResponse> simpleSearch(SimpleSearchDto simpleSearchDto) {
        var specification = productSpecification.buildSimpleSpecification(simpleSearchDto);
        var filteredProducts = productRepository.findAll(
                specification,
                PageRequest.of(simpleSearchDto.page(), simpleSearchDto.size())
        );

        return filteredProducts.map(mapper::toResponse);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Page<ProductServiceProductDetailedResponse> advancedSearch(
            Pageable pageable, List<AdvancedSearchParam<?>> filterParams
    ) {
        var specification = productSpecification.buildAdvancedSpecification(filterParams);
        var filteredProducts = productRepository.findAll(specification, pageable);

        return filteredProducts.map(mapper::toResponse);
    }

    // ---------------------------------- INTERACT METHODS FOR ORDER SERVICE -------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public ProductServiceReservationResponse reserveProductsWithLock(
            ProductServiceReservationCommand reserveCommand
    ) {
        final var foundEntitiesList = productRepository.findAllByIdInAndIsAvailableIsTrue(
                reserveCommand.productQuantities().keySet()
        );

        checkRequestToRepositoryResponseLength(reserveCommand.productQuantities(), foundEntitiesList);

        return processReservationsAndUpdateEntities(reserveCommand.productQuantities(), foundEntitiesList);
    }

// ---------------------------------- CURRENT SERVICE PRIVATE HELPERS --------------------------------------------------

    private void checkArticleUnique(String article) {
        var mbProduct = productRepository.findByArticle(article);
        if (mbProduct.isPresent()) {
            throw new NotUniqueArticleException(mbProduct.get().getId());
        }
    }

    private void validateArticleUniquenessIfChanged(ProductEntity existingProduct, @Nullable String newArticle) {
        if (newArticle == null || newArticle.equals(existingProduct.getArticle())) {
            return;
        }

        productRepository.findByArticle(newArticle)
                .ifPresent(conflictingProduct -> {
                    if (!conflictingProduct.getId().equals(existingProduct.getId())) {
                        throw new NotUniqueArticleException(conflictingProduct.getId());
                    }
                });
    }

    private ProductEntity getProductForUpdate(UUID id) {
        return productRepository.findByIdLocked(id)
                .orElseThrow(() -> new ResourceNotFoundException(ProductEntity.class, id));

    }

    private Set<UUID> getMissingIds(Set<UUID> requestReserveidSet, Set<UUID> foundedIdsSet) {
        var missingIdsSet = new HashSet<>(requestReserveidSet);
        missingIdsSet.removeAll(foundedIdsSet);

        return missingIdsSet;
    }

    private void checkRequestToRepositoryResponseLength(
            Map<UUID, BigDecimal> pickedProductQuantitiesMap,
            List<ProductEntity> foundEntitiesList
    ) {
        if (pickedProductQuantitiesMap.size() != foundEntitiesList.size()) {
            var missingIds = getMissingIds(
                    pickedProductQuantitiesMap.keySet(),
                    foundEntitiesList.stream()
                            .map(ProductEntity::getId)
                            .collect(Collectors.toSet())
            );
            throw new ProductsToOrderNotFoundException(missingIds);
        }
    }

    private ProductServiceReservationResponse processReservationsAndUpdateEntities(
            Map<UUID, BigDecimal> pickedProductQuantitiesMap,
            Collection<ProductEntity> foundEntitiesList
    ) {
        var reservedProductsResponseMap = new HashMap<UUID, ProductServiceReservedProductInfo>();

        foundEntitiesList.forEach(it -> {
            var valueToReserve = pickedProductQuantitiesMap.get(it.getId());
            var updateCommand = getChangeQuantityUpdateCommand(valueToReserve, it);
            mapper.update(it, updateCommand);

            reservedProductsResponseMap.put(
                    it.getId(),
                    mapper.toProductInfo(valueToReserve, it.getPrice())
            );
        });

        productRepository.saveAllAndFlush(foundEntitiesList);

        return mapper.toResponse(reservedProductsResponseMap);
    }

// ---------------------------------- Private Update Helpers -----------------------------------------------------------

    private ProductServiceUpdateCommand getChangeQuantityUpdateCommand(
            BigDecimal quantityToReserve,
            ProductEntity entity
    ) {
        var stockQuantity = entity.getQuantity();

        if (stockQuantity.compareTo(quantityToReserve) < 0) {
            throw new NotEnoughQuantityInStockException(entity.getId());
        }

        var quantityAfterReserve = stockQuantity.subtract(quantityToReserve);

        return ProductServiceUpdateCommand.builder()
                .quantity(quantityAfterReserve)
                .build();
    }
}
