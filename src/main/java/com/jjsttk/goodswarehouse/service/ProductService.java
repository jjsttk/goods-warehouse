package com.jjsttk.goodswarehouse.service;

import com.jjsttk.goodswarehouse.dto.request.CreateProductRequestDto;
import com.jjsttk.goodswarehouse.dto.request.UpdateProductRequestDto;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public final class ProductService {
    private final ProductRepository productRepository;
    private final ProductConverter productConverter;

    public List<ProductResponseDto> getAll() {
        return productRepository.findAll().stream()
                .map(productConverter::mapToDto)
                .toList();
    }

    public ProductResponseDto getById(UUID id) {
        return productRepository.findById(id)
                .map(productConverter::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionMessage.entityNotFoundMessage(Product.class, id)
                ));
    }

    public ProductResponseDto create(CreateProductRequestDto createDto) {
        checkArticleUnique(createDto.getArticle());
        DtoNormalizer.normalize(createDto);
        validateCategory(createDto.getCategory());

        Product entity = productConverter.mapToEntity(createDto);
        Product saved = productRepository.save(entity);

        return productConverter.mapToDto(saved);
    }

    public ProductResponseDto update(UpdateProductRequestDto updateDto, UUID id) {
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

    public void delete(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionMessage.entityNotFoundMessage(Product.class, id)));
        productRepository.delete(product);
    }


    private void checkArticleUnique(Long article) {
        productRepository.findByArticle(article)
                .ifPresent(p -> {
                    throw new ValidationException(
                            ValidationMessage.entityWithThisArticleExistsMessage(Product.class, article)
                    );
                });
    }

    private void validateCategory(String categoryStr) {
        try {
            Category.valueOf(categoryStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException(
                    ValidationMessage.categoryDoesNotExistsMessage(Category.class, categoryStr)
            );
        }
    }




    private void updateName(UpdateProductRequestDto dto, Product entity) {
        Optional.ofNullable(dto.getName())
                .ifPresent(name -> {
                    if (name.isBlank()) throw new ValidationException(
                            ValidationMessage.nameCannotBeBlankMessage());
                    entity.setName(name);
                });
    }

    private void updateDescription(UpdateProductRequestDto dto, Product entity) {
        Optional.ofNullable(dto.getDescription())
                .ifPresent(desc -> {
                    if (desc.isBlank()) throw new ValidationException(
                            ValidationMessage.descriptionCannotBeBlankMessage());
                    entity.setDescription(desc);
                });
    }

    private void updateCategory(UpdateProductRequestDto dto, Product entity) {
        Optional.ofNullable(dto.getCategory())
                .ifPresent(category -> {
                    if (category.isBlank()) throw new ValidationException(
                            ValidationMessage.categoryCannotBeBlankMessage());
                    try {
                        entity.setCategory(Category.valueOf(category.toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        throw new ValidationException(
                                ValidationMessage.categoryDoesNotExistsMessage(Category.class, category));
                    }
                });
    }

    private void updateArticle(UpdateProductRequestDto dto, Product entity) {
        Optional.ofNullable(dto.getArticle())
                .ifPresent(article -> {
                    if (article < 0) throw new ValidationException(
                            ValidationMessage.articleIsNotValidMessage());

                    boolean exists = productRepository.existsByArticleAndIdNot(article, entity.getId());
                    if (exists) throw new ValidationException(
                            ValidationMessage.notUniqueArticleMessage());

                    entity.setArticle(article);
                });
    }

    private void updatePrice(UpdateProductRequestDto dto, Product entity) {
        Optional.ofNullable(dto.getPrice())
                .ifPresent(price -> {
                    if (price.compareTo(BigDecimal.ZERO) <= 0)
                        throw new ValidationException(
                                ValidationMessage.priceIsNotValidMessage());
                    entity.setPrice(price);
                });
    }

    private void updateQuantity(UpdateProductRequestDto dto, Product entity) {
        Optional.ofNullable(dto.getQuantity())
                .ifPresent(quantity -> {
                    if (quantity < 0) throw new ValidationException(
                            ValidationMessage.quantityIsNotValidMessage());

                    if (!Objects.equals(entity.getQuantity(), quantity)) {
                        entity.setQuantity(quantity);
                        entity.setLastQuantityModified(LocalDateTime.now());
                    }
                });
    }
}
