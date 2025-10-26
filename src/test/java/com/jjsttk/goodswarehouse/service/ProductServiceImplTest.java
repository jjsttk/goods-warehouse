package com.jjsttk.goodswarehouse.service;

import com.jjsttk.goodswarehouse.enums.Category;
import com.jjsttk.goodswarehouse.enums.FilterOperation;
import com.jjsttk.goodswarehouse.exception.NotUniqueArticleException;
import com.jjsttk.goodswarehouse.exception.ResourceNotFoundException;
import com.jjsttk.goodswarehouse.mapper.ProductConverter;
import com.jjsttk.goodswarehouse.persistence.entity.ProductEntity;
import com.jjsttk.goodswarehouse.persistence.repository.ProductRepository;
import com.jjsttk.goodswarehouse.service.command.ProductCreateCommand;
import com.jjsttk.goodswarehouse.service.command.ProductUpdateCommand;
import com.jjsttk.goodswarehouse.service.response.ProductServiceResponse;
import com.jjsttk.goodswarehouse.service.search.ProductSpecification;
import com.jjsttk.goodswarehouse.service.search.advanced.param.AdvancedSearchParam;
import com.jjsttk.goodswarehouse.service.search.simple.SimpleSearchDto;
import com.jjsttk.goodswarehouse.service.search.advanced.param.StringParam;
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
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

    @Mock
    private ProductSpecification productSpecificationMock;

    private Specification<ProductEntity> specificationStub;
    private ProductEntity productEntityStub;
    private ProductCreateCommand createCommandStub;
    private ProductServiceResponse serviceResponseStub;
    private ProductUpdateCommand updateCommandStub;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        specificationStub = ProductTestDataFactory.getUnrestrictedProductSpecification();
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

        assertThat(serviceResponse.createdAt()).isEqualTo(productEntityStub.getCreatedAt());
        assertThat(serviceResponse.lastQuantityModified()).isNotEqualTo(productEntityStub.getLastQuantityModified());
        assertThat(serviceResponse).isEqualTo(serviceResponseStub);
        verify(repositoryMock, times(1)).save(productEntityStub);
    }

    @Test
    void createShouldThrowNotUniqueArticleExceptionWhenArticleIsNotUniqueAndCategoryValid() {
        createCommandStub.setArticle("existingArticle");

        when(repositoryMock.findByArticle("existingArticle"))
                .thenReturn(Optional.of(productEntityStub));

        assertThrows(
                NotUniqueArticleException.class,
                () -> sut.create(createCommandStub)
        );
        verify(repositoryMock, times(1)).findByArticle("existingArticle");
        verify(repositoryMock, never()).save(productEntityStub);

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

        when(mapperMock.mapToServiceResponse(any(ProductEntity.class)))
                .thenAnswer(invocation -> {
                    ProductEntity arg = invocation.getArgument(0);
                    int index = entitiesStub.indexOf(arg);
                    return serviceResponsesStub.get(index);
                });

        Page<ProductServiceResponse> result = sut.getAll(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(entitiesStub.size());
        assertThat(result.getContent()).containsExactlyElementsOf(serviceResponsesStub);

        verify(repositoryMock).findAll(pageable);
        verify(mapperMock, times(entitiesStub.size())).mapToServiceResponse(any(ProductEntity.class));
    }

    @Test
    void updateShouldNotChangeAnythingWhenAllFieldsNull() {
        updateCommandStub = ProductUpdateCommand.builder().build();
        productEntityStub.setLastQuantityModified(null);

        when(repositoryMock.findByIdLocked(productEntityStub.getId()))
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

        verify(repositoryMock, times(1)).findByIdLocked(any());
        verify(repositoryMock, times(1)).save(productEntityStub);
    }

    @Test
    void updateShouldNotModifyTimeInLastQuantityModifiedWhenQuantityDoesNotChanged() {
        updateCommandStub.setName("NewName");

        when(repositoryMock.findByIdLocked(productEntityStub.getId()))
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

        when(repositoryMock.findByIdLocked(productEntityStub.getId()))
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
        when(repositoryMock.findByIdLocked(productEntityStub.getId()))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> sut.update(updateCommandStub, productEntityStub.getId()));
        verify(repositoryMock, times(1)).findByIdLocked(any());
        verify(repositoryMock, never()).save(any());
    }

    @Test
    void updateShouldThrowNotUniqueArticleExceptionWhenArticleIsAlreadyExistsAndUsedByOtherProduct() {
        var id = productEntityStub.getId();
        var duplicateProductStub = ProductTestDataFactory.getProductEntityWithGeneratedId();
        duplicateProductStub.setArticle(productEntityStub.getArticle());

        when(repositoryMock.findByArticle(productEntityStub.getArticle()))
                .thenReturn(Optional.of(duplicateProductStub));

        assertThrows(NotUniqueArticleException.class,
                () -> sut.update(updateCommandStub, id));

        verify(repositoryMock, times(1)).findByArticle(any());
        verify(repositoryMock, never()).findByIdLocked(any());
        verify(repositoryMock, never()).save(any());
    }

    @Test
    void updateShouldModifyDescription() {
        updateCommandStub.setDescription("Updated Description");

        when(repositoryMock.findByIdLocked(productEntityStub.getId()))
                .thenReturn(Optional.of(productEntityStub));
        when(mapperMock.mapToServiceResponse(productEntityStub))
                .thenReturn(serviceResponseStub);

        sut.update(updateCommandStub, productEntityStub.getId());

        assertThat(productEntityStub.getDescription()).isEqualTo("Updated Description");
        verify(repositoryMock, times(1)).findByIdLocked(any());
        verify(mapperMock, times(1)).mapToServiceResponse(any());
        verify(repositoryMock, times(1)).save(productEntityStub);
    }

    @Test
    void updateShouldModifyCategory() {
        updateCommandStub.setCategory(Category.BEAUTY);

        when(repositoryMock.findByIdLocked(productEntityStub.getId()))
                .thenReturn(Optional.of(productEntityStub));
        when(mapperMock.mapToServiceResponse(productEntityStub))
                .thenReturn(serviceResponseStub);

        sut.update(updateCommandStub, productEntityStub.getId());

        assertThat(productEntityStub.getCategory()).isEqualTo(Category.BEAUTY);
        verify(repositoryMock, times(1)).findByIdLocked(any());
        verify(mapperMock, times(1)).mapToServiceResponse(any());
        verify(repositoryMock, times(1)).save(productEntityStub);
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
        verify(repositoryMock, never()).delete(any(ProductEntity.class));
    }

    @Test
    void updateShouldUpdateLastQuantityModifiedWhenQuantityChanges() {
        updateCommandStub.setQuantity(productEntityStub.getQuantity().add(BigDecimal.TWO));
        productEntityStub.setLastQuantityModified(null);

        when(repositoryMock.findByIdLocked(productEntityStub.getId()))
                .thenReturn(Optional.of(productEntityStub));
        when(mapperMock.mapToServiceResponse(productEntityStub))
                .thenReturn(serviceResponseStub);

        sut.update(updateCommandStub, productEntityStub.getId());

        assertThat(productEntityStub.getQuantity()).isEqualTo(updateCommandStub.getQuantity());
        assertThat(productEntityStub.getLastQuantityModified()).isNotNull();

        verify(repositoryMock, times(1)).findByIdLocked(any());
        verify(repositoryMock, times(1)).save(productEntityStub);
    }

    // ---------------- SimpleSearch ------------------
    @Test
    void simpleSearchShouldCallRepositoryAndMapResultsWhenDtoIsValid() {
        var entitiesStub = ProductTestDataFactory.getProductsList(5);
        var entityPageStub = new PageImpl<>(entitiesStub, pageable, entitiesStub.size());
        var simpleSearchDto = new SimpleSearchDto("product", BigDecimal.valueOf(100), BigDecimal.valueOf(10), 0, 20);
        var pageServiceResponseStub = ProductTestDataFactory.getServiceResponsesList(entitiesStub);

        when(productSpecificationMock.buildSimpleSpecification(simpleSearchDto)).thenReturn(specificationStub);

        when(repositoryMock.findAll(specificationStub, PageRequest.of(simpleSearchDto.page(), simpleSearchDto.size())))
                .thenReturn(entityPageStub);

        when(mapperMock.mapToServiceResponse(any(ProductEntity.class)))
                .thenAnswer(invocation -> {
                    ProductEntity arg = invocation.getArgument(0);
                    var index = entitiesStub.indexOf(arg);
                    return pageServiceResponseStub.get(index);
                });

        var result = sut.simpleSearch(simpleSearchDto);

        assertNotNull(result);
        verify(productSpecificationMock).buildSimpleSpecification(simpleSearchDto);
        verify(repositoryMock).findAll(
                specificationStub,
                PageRequest.of(simpleSearchDto.page(), simpleSearchDto.size())
        );
        verify(mapperMock, times(entityPageStub.getSize())).mapToServiceResponse(any());
    }

    @Test
    void simpleSearchShouldWorkWithNullFields() {
        var simpleSearchDto = new SimpleSearchDto(null, null, null, 0, 10);
        var entitiesStub = ProductTestDataFactory.getProductsList(5);
        var entityPageStub = new PageImpl<>(entitiesStub, pageable, entitiesStub.size());
        var pageServiceResponseStub = ProductTestDataFactory.getServiceResponsesList(entitiesStub);

        when(productSpecificationMock.buildSimpleSpecification(simpleSearchDto)).thenReturn(specificationStub);

        when(repositoryMock.findAll(specificationStub, PageRequest.of(simpleSearchDto.page(), simpleSearchDto.size())))
                .thenReturn(entityPageStub);

        when(mapperMock.mapToServiceResponse(any(ProductEntity.class)))
                .thenAnswer(invocation -> {
                    ProductEntity arg = invocation.getArgument(0);
                    var index = entitiesStub.indexOf(arg);
                    return pageServiceResponseStub.get(index);
                });

        var result = sut.simpleSearch(simpleSearchDto);

        assertNotNull(result);
        verify(productSpecificationMock).buildSimpleSpecification(simpleSearchDto);
        verify(repositoryMock).findAll(
                specificationStub,
                PageRequest.of(simpleSearchDto.page(), simpleSearchDto.size())
        );
    }

    @Test
    void advancedSearchShouldCallRepositoryAndMapResultsWhenFilterParamsProvided() {
        var entitiesStub = ProductTestDataFactory.getProductsList(5);
        var entityPageStub = new PageImpl<>(entitiesStub, pageable, entitiesStub.size());
        var pageServiceResponseStub = ProductTestDataFactory.getServiceResponsesList(entitiesStub);

        List<AdvancedSearchParam<?>> filterParams = List.of(new StringParam("name", "test", FilterOperation.LIKE));

        when(productSpecificationMock.buildAdvancedSpecification(filterParams)).thenReturn(specificationStub);
        when(repositoryMock.findAll(specificationStub, pageable)).thenReturn(entityPageStub);
        when(mapperMock.mapToServiceResponse(any(ProductEntity.class)))
                .thenAnswer(invocation -> {
                    ProductEntity arg = invocation.getArgument(0);
                    var index = entitiesStub.indexOf(arg);
                    return pageServiceResponseStub.get(index);
                });

        var result = sut.advancedSearch(pageable, filterParams);

        assertNotNull(result);
        verify(productSpecificationMock).buildAdvancedSpecification(filterParams);
        verify(repositoryMock).findAll(specificationStub, pageable);
        verify(mapperMock, times(entitiesStub.size())).mapToServiceResponse(any());
    }
}
