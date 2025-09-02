package com.jjsttk.goodswarehouse.service;

import com.jjsttk.goodswarehouse.exception.FieldValidationException;
import com.jjsttk.goodswarehouse.service.command.ProductCreateCommand;
import com.jjsttk.goodswarehouse.service.command.ProductUpdateCommand;
import com.jjsttk.goodswarehouse.service.response.ProductServiceResponse;
import com.jjsttk.goodswarehouse.exception.ResourceNotFoundException;
import com.jjsttk.goodswarehouse.mapper.ProductConverter;
import com.jjsttk.goodswarehouse.persistence.entity.ProductEntity;
import com.jjsttk.goodswarehouse.enums.Category;
import com.jjsttk.goodswarehouse.persistence.repository.ProductRepository;
import com.jjsttk.goodswarehouse.testutil.ProductTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {
    @InjectMocks
    private ProductServiceImpl sut;

    @Mock
    private ProductRepository repositoryMock;

    @Mock
    private ProductConverter mapperMock;

    private ProductEntity productEntityStub;
    private ProductCreateCommand createCommandStub;
    private ProductServiceResponse serviceResponseStub;
    private ProductUpdateCommand updateCommandStub;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        productEntityStub = ProductTestDataFactory.getProductEntityWithGeneratedId();
        createCommandStub = ProductTestDataFactory.getProductCreateCommand(productEntityStub);
        serviceResponseStub = ProductTestDataFactory.getProductServiceResponse(productEntityStub);
        updateCommandStub = ProductTestDataFactory.getProductUpdateCommand(productEntityStub);
        pageable = PageRequest.of(1, 5, Sort.by("name").ascending());
    }

    @Test
    void createShouldSaveEntityWhenArticleIsUniqueAndCategoryValid() {
        when(mapperMock.mapToEntity(createCommandStub))
                .thenReturn(productEntityStub);
        when(repositoryMock.save(productEntityStub))
                .thenReturn(productEntityStub);
        when(mapperMock.mapToServiceResponse(productEntityStub))
                .thenReturn(serviceResponseStub);

        var serviceResponse = sut.create(createCommandStub);

        assertThat(serviceResponse).isEqualTo(serviceResponseStub);
        verify(repositoryMock, times(1)).save(productEntityStub);
    }

    @Test
    void getByIdShouldReturnProductWhenProductExist() {
        var id = productEntityStub.getId();

        when(repositoryMock.findById(id))
                .thenReturn(Optional.of(productEntityStub));
        when(mapperMock.mapToServiceResponse(productEntityStub))
                .thenReturn(serviceResponseStub);

        var serviceResponse = sut.getById(id);

        assertThat(serviceResponse).isEqualTo(serviceResponseStub);
        verify(repositoryMock, times(1)).findById(id);
    }

    @Test
    void getByIdShouldThrowResourceNotFoundExceptionWhenProductDoesNotExist() {
        var id = UUID.randomUUID();

        when(repositoryMock.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> sut.getById(id));
    }

    @Test
    void getAllShouldReturnPageWithMappedProducts() {
        var entitiesStub = ProductTestDataFactory.getProductsList(45);
        var entityPageStub = new PageImpl<>(entitiesStub, pageable, entitiesStub.size());
        var serviceResponsesStub =
                ProductTestDataFactory.getServiceResponsesList(entitiesStub);

        when(repositoryMock.findAll(pageable))
                .thenReturn(entityPageStub);
        when(repositoryMock.findAll(pageable))
                .thenReturn(entityPageStub);

        when(mapperMock.mapToServiceResponse(any(ProductEntity.class)))
                .thenAnswer(invocation -> {
                    ProductEntity arg = invocation.getArgument(0);
                    int index = entitiesStub.indexOf(arg);
                    return serviceResponsesStub.get(index);
                });

        Page<ProductServiceResponse> result = sut.getAll(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(45);
        assertThat(result.getContent()).containsExactlyElementsOf(serviceResponsesStub);

        verify(repositoryMock).findAll(pageable);
        verify(mapperMock, times(45)).mapToServiceResponse(any(ProductEntity.class));
    }

    @Test
    void updateShouldNotChangeAnythingWhenAllFieldsNull() {
        updateCommandStub = ProductUpdateCommand.builder().build();
        productEntityStub.setLastQuantityModified(null);

        when(repositoryMock.findById(productEntityStub.getId()))
                .thenReturn(Optional.of(productEntityStub));
        when(mapperMock.mapToServiceResponse(productEntityStub))
                .thenReturn(serviceResponseStub);

        sut.update(updateCommandStub, productEntityStub.getId());

        assertThat(productEntityStub.getName()).isNotEqualTo(null);
        assertThat(productEntityStub.getDescription()).isNotEqualTo(null);
        assertThat(productEntityStub.getCategory()).isNotEqualTo(null);
        assertThat(productEntityStub.getArticle()).isNotEqualTo(null);
        assertThat(productEntityStub.getPrice()).isNotEqualTo(null);
        assertThat(productEntityStub.getQuantity()).isNotEqualTo(null);
        assertThat(productEntityStub.getLastQuantityModified()).isNull();

        verify(repositoryMock, times(1)).findById(any());
        verify(repositoryMock, times(1)).save(productEntityStub);
    }

    @Test
    void updateShouldNotModifyTimeInLastQuantityModifiedWhenQuantityDoesNotChanged() {
        updateCommandStub.setName("NewName");

        when(repositoryMock.findById(productEntityStub.getId()))
                .thenReturn(Optional.of(productEntityStub));
        when(mapperMock.mapToServiceResponse(productEntityStub))
                .thenReturn(serviceResponseStub);

        var result = sut.update(updateCommandStub, productEntityStub.getId());

        assertThat(productEntityStub.getName()).isEqualTo(updateCommandStub.getName());
        assertThat(productEntityStub.getQuantity()).isEqualTo(updateCommandStub.getQuantity());
        assertThat(productEntityStub.getLastQuantityModified()).isEqualTo(result.lastQuantityModified());

        verify(repositoryMock, times(1)).save(productEntityStub);
        verify(mapperMock, times(1)).mapToServiceResponse(productEntityStub);
    }

    @Test
    void updateShouldModifyFieldsWhenValidUpdateCommandDto() {
        updateCommandStub.setName("NewName");
        updateCommandStub.setPrice(new BigDecimal("150.00"));
        updateCommandStub.setQuantity(productEntityStub.getQuantity().add(BigDecimal.ONE));
        updateCommandStub.setArticle(productEntityStub.getArticle() + 1);

        when(repositoryMock.findById(productEntityStub.getId()))
                .thenReturn(Optional.of(productEntityStub));
        when(mapperMock.mapToServiceResponse(productEntityStub))
                .thenReturn(serviceResponseStub);

        sut.update(updateCommandStub, productEntityStub.getId());

        assertThat(productEntityStub.getName()).isEqualTo(updateCommandStub.getName());
        assertThat(productEntityStub.getPrice()).isEqualByComparingTo(updateCommandStub.getPrice());
        assertThat(productEntityStub.getQuantity()).isEqualTo(updateCommandStub.getQuantity());
        assertThat(productEntityStub.getArticle()).isEqualTo(updateCommandStub.getArticle());
        assertThat(productEntityStub.getLastQuantityModified()).isNotNull();

        verify(repositoryMock, times(1)).save(productEntityStub);
        verify(mapperMock, times(1)).mapToServiceResponse(productEntityStub);
    }

    @Test
    void updateShouldThrowNotFoundExceptionWhenProductDoesNotExist() {
        when(repositoryMock.findById(productEntityStub.getId()))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> sut.update(updateCommandStub, productEntityStub.getId()));
        verify(repositoryMock, times(1)).findById(any());
        verify(repositoryMock, never()).save(any());
    }

    @Test
    void updateShouldThrowValidationExceptionWhenArticleIsAlreadyExistsAndUsedByOtherProduct() {
        var id = productEntityStub.getId();
        var duplicateProductStub = ProductTestDataFactory.getProductEntityWithGeneratedId();
        duplicateProductStub.setArticle(productEntityStub.getArticle());

        when(repositoryMock.findById(id))
                .thenReturn(Optional.of(productEntityStub));
        when(repositoryMock.findByArticle(productEntityStub.getArticle()))
                .thenReturn(Optional.of(duplicateProductStub));

        assertThrows(FieldValidationException.class,
                () -> sut.update(updateCommandStub, id));

        verify(repositoryMock, times(1)).findById(any());
        verify(repositoryMock, times(1)).findByArticle(any());
        verify(repositoryMock, never()).save(any());
    }

    @Test
    void updateShouldModifyDescription() {
        updateCommandStub.setDescription("Updated Description");

        when(repositoryMock.findById(productEntityStub.getId()))
                .thenReturn(Optional.of(productEntityStub));
        when(mapperMock.mapToServiceResponse(productEntityStub))
                .thenReturn(serviceResponseStub);

        sut.update(updateCommandStub, productEntityStub.getId());

        assertThat(productEntityStub.getDescription()).isEqualTo("Updated Description");
        verify(repositoryMock, times(1)).findById(any());
        verify(mapperMock, times(1)).mapToServiceResponse(any());
        verify(repositoryMock, times(1)).save(productEntityStub);
    }

    @Test
    void updateShouldModifyCategory() {
        updateCommandStub.setCategory(Category.BEAUTY);

        when(repositoryMock.findById(productEntityStub.getId()))
                .thenReturn(Optional.of(productEntityStub));
        when(mapperMock.mapToServiceResponse(productEntityStub))
                .thenReturn(serviceResponseStub);

        sut.update(updateCommandStub, productEntityStub.getId());

        assertThat(productEntityStub.getCategory()).isEqualTo(Category.BEAUTY);
        verify(repositoryMock, times(1)).findById(any());
        verify(mapperMock, times(1)).mapToServiceResponse(any());
        verify(repositoryMock, times(1)).save(productEntityStub);
    }

    @Test
    void updateShouldThrowMapOfValidationExceptionsWithAllFieldsInvalidInFirstTriggerSet() {
        var firstTriggerSet = updateCommandStub;
        firstTriggerSet.setArticle(" ");
        firstTriggerSet.setPrice(new BigDecimal("100.0005"));
        firstTriggerSet.setName(" ");
        firstTriggerSet.setQuantity(new BigDecimal("-1"));

        when(repositoryMock.findById(productEntityStub.getId()))
                .thenReturn(Optional.of(productEntityStub));

        var exception = assertThrows(FieldValidationException.class,
                () -> sut.update(firstTriggerSet, productEntityStub.getId()));

        Map<String, List<String>> errors = exception.getValidationErrors();

        assertThat(errors).containsKey("article");
        assertThat(errors.get("article")).contains("Article must not be blank");

        assertThat(errors).containsKey("price");
        assertThat(errors.get("price")).contains("Price must have up to 8 digits before decimal and 2 after");

        assertThat(errors).containsKey("name");
        assertThat(errors.get("name")).contains("Name must not be blank");

        assertThat(errors).containsKey("quantity");
        assertThat(errors.get("quantity")).contains("Quantity must be positive or zero");

        verify(repositoryMock, times(1)).findById(any());
        verify(repositoryMock, never()).save(any());
        verify(mapperMock, never()).mapToServiceResponse(any());

    }

    @Test
    void updateShouldThrowMapOfValidationExceptionsWithAllFieldsInvalidInSecondTriggerSet() {
        var secondTriggerSet = updateCommandStub;
        secondTriggerSet.setArticle(ProductTestDataFactory.getStringByLength(110));
        secondTriggerSet.setPrice(new BigDecimal("-1"));
        secondTriggerSet.setName(ProductTestDataFactory.getStringByLength(60));
        secondTriggerSet.setQuantity(new BigDecimal("1.1112"));

        when(repositoryMock.findById(productEntityStub.getId()))
                .thenReturn(Optional.of(productEntityStub));

        var exception = assertThrows(FieldValidationException.class,
                () -> sut.update(secondTriggerSet, productEntityStub.getId()));

        Map<String, List<String>> errors = exception.getValidationErrors();

        assertThat(errors).containsKey("article");
        assertThat(errors.get("article")).contains("Article must be no longer than 100 characters");

        assertThat(errors).containsKey("price");
        assertThat(errors.get("price")).contains("Price must be positive");

        assertThat(errors).containsKey("name");
        assertThat(errors.get("name")).contains("Name must be no longer than 50 characters");

        assertThat(errors).containsKey("quantity");
        assertThat(errors.get("quantity")).contains("Quantity must have up to 9 digits before decimal and 3 after");

        verify(repositoryMock, times(1)).findById(any());
        verify(repositoryMock, never()).save(any());
        verify(mapperMock, never()).mapToServiceResponse(any());
    }



    @Test
    void deleteShouldDeleteProductWhenExists() {
        when(repositoryMock.findById(productEntityStub.getId()))
                .thenReturn(Optional.of(productEntityStub));

        sut.delete(productEntityStub.getId());

        verify(repositoryMock, times(1)).findById(any());
        verify(repositoryMock, times(1)).delete(productEntityStub);
    }

    @Test
    void deleteShouldThrowNotFoundExceptionWhenProductDoesNotExist() {
        var randomId = UUID.randomUUID();

        when(repositoryMock.findById(randomId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> sut.delete(randomId));
        verify(repositoryMock, times(1)).findById(any());
        verify(repositoryMock, never()).delete(any());
    }

    @Test
    void updateShouldUpdateLastQuantityModifiedWhenQuantityChanges() {
        updateCommandStub.setQuantity(productEntityStub.getQuantity().add(BigDecimal.TWO));
        productEntityStub.setLastQuantityModified(null);

        when(repositoryMock.findById(productEntityStub.getId()))
                .thenReturn(Optional.of(productEntityStub));
        when(mapperMock.mapToServiceResponse(productEntityStub))
                .thenReturn(serviceResponseStub);

        sut.update(updateCommandStub, productEntityStub.getId());

        assertThat(productEntityStub.getQuantity()).isEqualTo(updateCommandStub.getQuantity());
        assertThat(productEntityStub.getLastQuantityModified()).isNotNull();

        verify(repositoryMock, times(1)).findById(any());
        verify(repositoryMock, times(1)).save(productEntityStub);
    }
}
