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
import com.jjsttk.goodswarehouse.service.product.search.advanced.param.StringParam;
import com.jjsttk.goodswarehouse.service.product.search.simple.SimpleSearchDto;
import com.jjsttk.goodswarehouse.service.product.search.specification.ProductSpecification;
import com.jjsttk.goodswarehouse.shared.enums.search.FilterOperation;
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
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    private ProductServiceConverter mapperMock;

    @Mock
    private ProductSpecification productSpecificationMock;

    private Specification<ProductEntity> specificationStub;
    private ProductEntity productEntityStub;
    private ProductServiceCreateCommand createCommandStub;
    private ProductServiceProductDetailedResponse serviceResponseStub;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        specificationStub = ProductTestDataFactory.getUnrestrictedProductSpecification();
        productEntityStub = ProductTestDataFactory.getProductEntityWithGeneratedId();
        createCommandStub = ProductTestDataFactory.getProductCreateCommand(productEntityStub);
        serviceResponseStub = ProductTestDataFactory.getProductServiceResponse(productEntityStub);
        pageable = PageRequest.of(1, 5, Sort.by("name").ascending());
    }

    @Test
    void createShouldSaveEntityWhenArticleIsUniqueAndCategoryValid() {
        when(mapperMock.toEntity(createCommandStub))
                .thenReturn(productEntityStub);
        when(repositoryMock.save(productEntityStub))
                .thenReturn(productEntityStub);
        when(mapperMock.toResponse(productEntityStub))
                .thenReturn(serviceResponseStub);

        var serviceResponse = sut.create(createCommandStub);

        assertThat(serviceResponse.createdAt()).isEqualTo(productEntityStub.getCreatedAt());
        assertThat(serviceResponse.lastQuantityModified()).isEqualTo(productEntityStub.getLastQuantityModified());
        assertThat(serviceResponse).isEqualTo(serviceResponseStub);
        verify(repositoryMock, times(1)).save(productEntityStub);
    }

    @Test
    void createShouldThrowNotUniqueArticleExceptionWhenArticleIsNotUniqueAndCategoryValid() {
        var commandStub = ProductServiceCreateCommand.builder()
                .name(productEntityStub.getName())
                .description(productEntityStub.getDescription())
                .article("existingArticle")
                .isAvailable(true)
                .quantity(productEntityStub.getQuantity())
                .price(productEntityStub.getPrice())
                .category(productEntityStub.getCategory())
                .build();

        when(repositoryMock.findByArticle("existingArticle"))
                .thenReturn(Optional.of(productEntityStub));

        assertThrows(
                NotUniqueArticleException.class,
                () -> sut.create(commandStub)
        );
        verify(repositoryMock, times(1)).findByArticle("existingArticle");
        verify(repositoryMock, never()).save(productEntityStub);

    }


    @Test
    void getByIdShouldReturnProductWhenProductExist() {
        var id = productEntityStub.getId();

        when(repositoryMock.findById(id))
                .thenReturn(Optional.of(productEntityStub));
        when(mapperMock.toResponse(productEntityStub))
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

        when(mapperMock.toResponse(any(ProductEntity.class)))
                .thenAnswer(invocation -> {
                    ProductEntity arg = invocation.getArgument(0);
                    int index = entitiesStub.indexOf(arg);
                    return serviceResponsesStub.get(index);
                });

        Page<ProductServiceProductDetailedResponse> result = sut.getAll(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(entitiesStub.size());
        assertThat(result.getContent()).containsExactlyElementsOf(serviceResponsesStub);

        verify(repositoryMock).findAll(pageable);
        verify(mapperMock, times(entitiesStub.size())).toResponse(any(ProductEntity.class));
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

        when(mapperMock.toResponse(any(ProductEntity.class)))
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
        verify(mapperMock, times(entityPageStub.getSize())).toResponse(any(ProductEntity.class));
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

        when(mapperMock.toResponse(any(ProductEntity.class)))
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
        when(mapperMock.toResponse(any(ProductEntity.class)))
                .thenAnswer(invocation -> {
                    ProductEntity arg = invocation.getArgument(0);
                    var index = entitiesStub.indexOf(arg);
                    return pageServiceResponseStub.get(index);
                });

        var result = sut.advancedSearch(pageable, filterParams);

        assertNotNull(result);
        verify(productSpecificationMock).buildAdvancedSpecification(filterParams);
        verify(repositoryMock).findAll(specificationStub, pageable);
        verify(mapperMock, times(entitiesStub.size())).toResponse(any(ProductEntity.class));
    }

    @Test
    void updateShouldCheckArticleUniquenessAndUpdateProduct() {
        var updateDto = ProductServiceUpdateCommand.builder()
                .article("newArticleTest")
                .build();
        when(repositoryMock.findByIdLocked(productEntityStub.getId()))
                .thenReturn(Optional.of(productEntityStub));
        when(repositoryMock.findByArticle(updateDto.article()))
                .thenReturn(Optional.empty());

        sut.update(productEntityStub.getId(), updateDto);
        verify(repositoryMock).findByIdLocked(productEntityStub.getId());
        verify(repositoryMock).findByArticle(updateDto.article());
        verify(mapperMock, times(1)).update(productEntityStub, updateDto);
        verify(mapperMock, times(1)).toResponse(productEntityStub);
    }

    @Test
    void updateShouldCheckArticleUniquenessAndThrowDuplicateArticleException() {
        var duplicateEntity = ProductTestDataFactory.getProductEntityWithGeneratedId();

        String newArticle = "DIFFERENT_ARTICLE";

        duplicateEntity.setArticle(newArticle);
        var updateCommand = ProductServiceUpdateCommand.builder()
                .article(newArticle)
                .build();

        when(repositoryMock.findByIdLocked(productEntityStub.getId()))
                .thenReturn(Optional.of(productEntityStub));
        when(repositoryMock.findByArticle(newArticle))
                .thenReturn(Optional.of(duplicateEntity));

        assertThrows(NotUniqueArticleException.class, () ->
                sut.update(productEntityStub.getId(), updateCommand));

        verify(repositoryMock).findByIdLocked(productEntityStub.getId());
        verify(repositoryMock).findByArticle(newArticle);
        verify(mapperMock, never()).update(any(), any());
        verify(mapperMock, never()).toResponse(any(ProductEntity.class));
    }

    @Test
    public void reserveProductShouldCallRepositoryAndMapResultsWhenProductsFound() {
        var valueToReserve = productEntityStub.getQuantity();
        var newQuantity = productEntityStub.getQuantity().subtract(valueToReserve);

        var productServiceReservationCommand = ProductServiceReservationCommand.builder()
                .productQuantities(Map.of(productEntityStub.getId(), valueToReserve))
                .build();

        var expectedUpdateCommand =
                ProductServiceUpdateCommand.builder()
                        .quantity(newQuantity)
                        .build();

        var entityListStub = List.of(productEntityStub);

        var productServiceReservedProductInfo = ProductServiceReservedProductInfo.builder()
                .reservedQuantity(valueToReserve)
                .priceAtMoment(productEntityStub.getPrice())
                .build();

        var reservedProductInfoStub = Map.of(productEntityStub.getId(), productServiceReservedProductInfo);

        var reserveResponseStub = ProductServiceReservationResponse.builder()
                .productInfo(reservedProductInfoStub)
                .build();

        when(repositoryMock.findAllByIdInAndIsAvailableIsTrue(
                productServiceReservationCommand.productQuantities().keySet()
        ))
                .thenReturn(entityListStub);
        when(mapperMock.toProductInfo(any(BigDecimal.class), any(BigDecimal.class)))
                .thenReturn(productServiceReservedProductInfo);
        when(mapperMock.toResponse(reservedProductInfoStub))
                .thenReturn(reserveResponseStub);

        var result =
                sut.reserveProductsWithLock(productServiceReservationCommand);

        verify(repositoryMock).findAllByIdInAndIsAvailableIsTrue(
                Set.of(productEntityStub.getId())
        );
        verify(mapperMock).update(productEntityStub, expectedUpdateCommand);
        verify(mapperMock).toProductInfo(valueToReserve, productEntityStub.getPrice());
        verify(mapperMock).toResponse(reservedProductInfoStub);
        verify(repositoryMock).saveAllAndFlush(entityListStub);

        assertEquals(reserveResponseStub, result);
    }

    @Test
    public void reserveProductsShouldThrowExceptionWhenSomeProductsNotFound() {
        var existingProductId = productEntityStub.getId();
        var missingProductId = UUID.randomUUID();

        ProductServiceReservationCommand reservationCommand =
                ProductServiceReservationCommand.builder()
                        .productQuantities(Map.of(
                                existingProductId, productEntityStub.getQuantity(),
                                missingProductId, new BigDecimal("2")
                        ))
                        .build();

        when(repositoryMock.findAllByIdInAndIsAvailableIsTrue(
                Set.of(existingProductId, missingProductId)
        )).thenReturn(List.of(productEntityStub));

        var exception = assertThrows(
                ProductsToOrderNotFoundException.class,
                () -> sut.reserveProductsWithLock(reservationCommand)
        );


        assertTrue(exception.getMessage().contains(String.valueOf(missingProductId)));

        verify(repositoryMock).findAllByIdInAndIsAvailableIsTrue(
                Set.of(existingProductId, missingProductId)
        );

        verify(mapperMock, never()).update(any(), any());
        verify(repositoryMock, never()).saveAllAndFlush(any());
    }

    @Test
    public void reserveProductsShouldThrowNotEnoughQuantityInStockExceptionWhenSomeProductsDontHaveQuantity() {
        var existingProductId = productEntityStub.getId();

        ProductServiceReservationCommand reservationCommand =
                ProductServiceReservationCommand.builder()
                        .productQuantities(Map.of(
                                existingProductId, productEntityStub.getQuantity().add(BigDecimal.ONE)
                        ))
                        .build();

        when(repositoryMock.findAllByIdInAndIsAvailableIsTrue(
                Set.of(existingProductId)
        )).thenReturn(List.of(productEntityStub));

        var exception = assertThrows(
                NotEnoughQuantityInStockException.class,
                () -> sut.reserveProductsWithLock(reservationCommand)
        );


        assertThat(exception.getMessage()).contains("Not enough quantity in stock for product productId = ");

        verify(repositoryMock).findAllByIdInAndIsAvailableIsTrue(
                Set.of(existingProductId)
        );

        verify(mapperMock, never()).update(any(), any());
        verify(repositoryMock, never()).saveAllAndFlush(any());
    }
}
