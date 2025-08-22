package com.jjsttk.goodswarehouse.unit.service;

import com.jjsttk.goodswarehouse.dto.request.ProductRequestCreateDto;
import com.jjsttk.goodswarehouse.dto.request.ProductRequestUpdateDto;
import com.jjsttk.goodswarehouse.dto.response.ProductResponseDto;
import com.jjsttk.goodswarehouse.exception.ResourceNotFoundException;
import com.jjsttk.goodswarehouse.mapper.ProductConverter;
import com.jjsttk.goodswarehouse.model.entity.Product;
import com.jjsttk.goodswarehouse.model.enums.Category;
import com.jjsttk.goodswarehouse.repository.ProductRepository;
import com.jjsttk.goodswarehouse.service.ProductService;
import com.jjsttk.goodswarehouse.testutil.ProductTestDataFactory;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductConverter productConverter;

    private Product product;
    private ProductRequestCreateDto createDto;
    private ProductResponseDto responseDto;
    private ProductRequestUpdateDto updateDto;

    @BeforeEach
    void setUp() {
        product = ProductTestDataFactory.getProductEntityWithGeneratedId();
        createDto = ProductTestDataFactory.getCreateProductRequestDto(product);
        responseDto = ProductTestDataFactory.getProductResponseDto(product);
        updateDto = ProductTestDataFactory.getUpdateProductRequestDto(product);
    }

    @Test
    void createShouldSaveProductWhenArticleIsUniqueAndCategoryValid() {
        when(productRepository.findByArticle(createDto.getArticle())).thenReturn(Optional.empty());
        when(productConverter.mapToEntity(createDto)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productConverter.mapToDto(product)).thenReturn(responseDto);

        var result = productService.create(createDto);

        assertThat(result).isEqualTo(responseDto);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void createShouldThrowValidationExceptionWhenArticleAlreadyExists() {
        when(productRepository.findByArticle(createDto.getArticle())).thenReturn(Optional.of(product));

        assertThrows(ValidationException.class, () -> productService.create(createDto));
        verify(productRepository, never()).save(any());
    }

    @Test
    void createShouldThrowValidationExceptionWhenCategoryNotFound() {
        createDto.setCategory("doesNotExist");

        when(productRepository.findByArticle(createDto.getArticle())).thenReturn(Optional.empty());
        assertThrows(ValidationException.class, () -> productService.create(createDto));
        verify(productRepository, never()).save(any());
    }



    @Test
    void getByIdShouldReturnProductWhenProductExist() {
        var id = product.getId();
        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productConverter.mapToDto(product)).thenReturn(responseDto);

        var result = productService.getById(id);

        assertThat(result).isEqualTo(responseDto);
        verify(productRepository, times(1)).findById(id);
    }

    @Test
    void getByIdShouldThrowResourceNotFoundExceptionWhenProductDoesNotExist() {
        var id = UUID.randomUUID();
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getById(id));
    }



    @Test
    void getAllShouldReturnEmptyListWhenNoProductsExist() {
        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        var result = productService.getAll();

        assertThat(result).isEqualTo(Collections.emptyList());
        verify(productRepository, times(1)).findAll();
        verify(productConverter, never()).mapToDto(any());

    }

    @Test
    void getAllShouldReturnListOfProductsWhenProductsExist() {
        var anotherProduct = ProductTestDataFactory.getProductEntityWithGeneratedId();
        var anotherProductResponseDto = ProductTestDataFactory.getProductResponseDto(anotherProduct);
        var productList = List.of(product, anotherProduct);
        var responseDtoList = List.of(responseDto, anotherProductResponseDto);

        when(productRepository.findAll()).thenReturn(productList);
        when(productConverter.mapToDto(product)).thenReturn(responseDto);
        when(productConverter.mapToDto(anotherProduct)).thenReturn(anotherProductResponseDto);

        assertThat(productService.getAll()).isEqualTo(responseDtoList);
        verify(productConverter, times(1)).mapToDto(product);
        verify(productConverter, times(1)).mapToDto(anotherProduct);
        verify(productRepository, times(1)).findAll();
    }



    @Test
    void updateShouldNotChangeAnythingWhenAllFieldsNull() {
        updateDto = ProductRequestUpdateDto.builder().build();
        product.setLastQuantityModified(null);

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(productConverter.mapToDto(product)).thenReturn(responseDto);

        productService.update(updateDto, product.getId());

        assertThat(product.getName()).isNotEqualTo(null);
        assertThat(product.getDescription()).isNotEqualTo(null);
        assertThat(product.getCategory()).isNotEqualTo(null);
        assertThat(product.getArticle()).isNotEqualTo(null);
        assertThat(product.getPrice()).isNotEqualTo(null);
        assertThat(product.getQuantity()).isNotEqualTo(null);
        assertThat(product.getLastQuantityModified()).isNull();

        verify(productRepository, times(1)).findById(any());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void updateShouldNotModifyTimeInLastQuantityModifiedWhenQuantityDoesNotChanged() {
        updateDto.setName("NewName");

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(productConverter.mapToDto(product)).thenReturn(responseDto);

        var result = productService.update(updateDto, product.getId());

        assertThat(product.getName()).isEqualTo(updateDto.getName());
        assertThat(product.getQuantity()).isEqualTo(updateDto.getQuantity());
        assertThat(product.getLastQuantityModified()).isEqualTo(result.lastQuantityModified());

        verify(productRepository, times(1)).save(product);
        verify(productConverter, times(1)).mapToDto(product);
    }

    @Test
    void updateShouldModifyFieldsWhenValidUpdateDto() {
        updateDto.setName("NewName");
        updateDto.setPrice(new BigDecimal("150.00"));
        updateDto.setQuantity(product.getQuantity() + 1);
        updateDto.setArticle(product.getArticle() + 1);

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(productConverter.mapToDto(product)).thenReturn(responseDto);

        productService.update(updateDto, product.getId());

        assertThat(product.getName()).isEqualTo(updateDto.getName());
        assertThat(product.getPrice()).isEqualByComparingTo(updateDto.getPrice());
        assertThat(product.getQuantity()).isEqualTo(updateDto.getQuantity());
        assertThat(product.getArticle()).isEqualTo(updateDto.getArticle());
        assertThat(product.getLastQuantityModified()).isNotNull();

        verify(productRepository, times(1)).save(product);
        verify(productConverter, times(1)).mapToDto(product);
    }

    @Test
    void updateShouldThrowNotFoundWhenProductDoesNotExist() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.update(updateDto, product.getId()));
        verify(productRepository, times(1)).findById(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateShouldThrowValidationExceptionWhenArticleIsNotValid() {
        updateDto.setArticle(-332L);

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        assertThrows(ValidationException.class, () -> productService.update(updateDto, product.getId()));
        verify(productRepository, times(1)).findById(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateShouldThrowValidationExceptionWhenQuantityIsNotValid() {
        updateDto.setQuantity(-15);

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        assertThrows(ValidationException.class, () -> productService.update(updateDto, product.getId()));
        verify(productRepository, times(1)).findById(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateShouldThrowValidationExceptionWhenNameIsNotValid() {
        updateDto.setName("         ");

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        assertThrows(ValidationException.class, () -> productService.update(updateDto, product.getId()));
        verify(productRepository, times(1)).findById(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateShouldThrowValidationExceptionWhenInvalidPrice() {
        updateDto.setPrice(new BigDecimal("-100"));

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        assertThrows(ValidationException.class, () -> productService.update(updateDto, product.getId()));
        verify(productRepository, times(1)).findById(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateShouldThrowValidationExceptionWhenArticleAlreadyExists() {
        var id = product.getId();

        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productRepository.existsByArticleAndIdNot(product.getArticle(), id)).thenReturn(true);

        assertThrows(ValidationException.class,
                () -> productService.update(updateDto, id));

        verify(productRepository, times(1)).findById(any());
        verify(productRepository, times(1)).existsByArticleAndIdNot(any(), any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateShouldThrowValidationExceptionWhenDescriptionBlank() {
        updateDto.setDescription("   ");

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        assertThrows(ValidationException.class, () -> productService.update(updateDto, product.getId()));
        verify(productRepository, times(1)).findById(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateShouldThrowValidationExceptionWhenCategoryBlank() {
        updateDto.setCategory("   ");

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        assertThrows(ValidationException.class, () -> productService.update(updateDto, product.getId()));
        verify(productRepository, times(1)).findById(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateShouldThrowValidationExceptionWhenCategoryInvalid() {
        updateDto.setCategory("invalid_category");

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        assertThrows(ValidationException.class, () -> productService.update(updateDto, product.getId()));
        verify(productRepository, times(1)).findById(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateShouldModifyDescriptionWhenValid() {
        updateDto.setDescription("Updated Description");

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(productConverter.mapToDto(product)).thenReturn(responseDto);

        productService.update(updateDto, product.getId());

        assertThat(product.getDescription()).isEqualTo("Updated Description");
        verify(productRepository, times(1)).findById(any());
        verify(productConverter, times(1)).mapToDto(any());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void updateShouldModifyCategoryWhenValid() {
        updateDto.setCategory("electronics");

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(productConverter.mapToDto(product)).thenReturn(responseDto);

        productService.update(updateDto, product.getId());

        assertThat(product.getCategory()).isEqualTo(Category.ELECTRONICS);
        verify(productRepository, times(1)).findById(any());
        verify(productConverter, times(1)).mapToDto(any());
        verify(productRepository, times(1)).save(product);
    }



    @Test
    void deleteShouldDeleteProductWhenExists() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        productService.delete(product.getId());

        verify(productRepository, times(1)).findById(any());
        verify(productRepository, times(1)).delete(product);
    }

    @Test
    void deleteShouldThrowNotFoundExceptionWhenProductDoesNotExist() {
        var randomId = UUID.randomUUID();

        when(productRepository.findById(randomId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.delete(randomId));
        verify(productRepository, times(1)).findById(any());
        verify(productRepository, never()).delete(any());
    }

    @Test
    void updateShouldUpdateLastQuantityModifiedWhenQuantityChanges() {
        updateDto.setQuantity(product.getQuantity() + 5);
        product.setLastQuantityModified(null);

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(productConverter.mapToDto(product)).thenReturn(responseDto);

        productService.update(updateDto, product.getId());

        assertThat(product.getQuantity()).isEqualTo(updateDto.getQuantity());
        assertThat(product.getLastQuantityModified()).isNotNull();

        verify(productRepository, times(1)).findById(any());
        verify(productRepository, times(1)).save(product);
    }
}
