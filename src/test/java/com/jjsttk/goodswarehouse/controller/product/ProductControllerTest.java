package com.jjsttk.goodswarehouse.controller.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjsttk.goodswarehouse.controller.product.dto.request.CreateProductRequest;
import com.jjsttk.goodswarehouse.controller.product.dto.request.UpdateProductRequest;
import com.jjsttk.goodswarehouse.controller.product.dto.response.GetProductResponse;
import com.jjsttk.goodswarehouse.controller.product.dto.response.PageGetProductResponse;
import com.jjsttk.goodswarehouse.exception.handler.GlobalExceptionHandler;
import com.jjsttk.goodswarehouse.exception.service.ResourceNotFoundException;
import com.jjsttk.goodswarehouse.exception.service.product.NotUniqueArticleException;
import com.jjsttk.goodswarehouse.mapper.product.ProductControllerConverter;
import com.jjsttk.goodswarehouse.persistence.entity.product.ProductEntity;
import com.jjsttk.goodswarehouse.service.exchange.ExchangeRateService;
import com.jjsttk.goodswarehouse.service.exchange.dto.response.ExchangeRate;
import com.jjsttk.goodswarehouse.service.product.ProductService;
import com.jjsttk.goodswarehouse.service.product.dto.command.CreateProductCommandInfo;
import com.jjsttk.goodswarehouse.service.product.dto.command.UpdateProductCommandInfo;
import com.jjsttk.goodswarehouse.service.product.dto.response.ProductDetailedResponse;
import com.jjsttk.goodswarehouse.service.product.search.advanced.param.AdvancedSearchParam;
import com.jjsttk.goodswarehouse.service.product.search.advanced.param.StringParam;
import com.jjsttk.goodswarehouse.service.product.search.simple.SimpleSearchDto;
import com.jjsttk.goodswarehouse.shared.enums.exchange.PriceCurrency;
import com.jjsttk.goodswarehouse.shared.enums.product.Category;
import com.jjsttk.goodswarehouse.shared.enums.search.FilterOperation;
import com.jjsttk.goodswarehouse.testutil.ProductTestDataFactory;
import com.jjsttk.goodswarehouse.testutil.StringTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private ProductControllerConverter productControllerConverterMock;

    @Mock
    private ProductService productServiceMock;

    @Mock
    private ExchangeRateService exchangeRateServiceMock;

    @InjectMocks
    private ProductControllerImpl sut;

    private CreateProductRequest controllerRequestStub;
    private GetProductResponse controllerResponseStub;

    private CreateProductCommandInfo createCommandStub;
    private ProductDetailedResponse serviceResponseStub;
    private ExchangeRate exchangeRateServiceResponseRubStub;

    private ProductEntity productEntityStub;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(sut)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        objectMapper = new ObjectMapper();

        productEntityStub = ProductTestDataFactory.getProductEntityWithGeneratedId();
        controllerRequestStub = ProductTestDataFactory.getCreateProductRequest(productEntityStub);
        controllerResponseStub = ProductTestDataFactory.getGetProductResponse(productEntityStub);

        createCommandStub = ProductTestDataFactory.getCreateProductCommand(productEntityStub);
        serviceResponseStub = ProductTestDataFactory.getProductDetailedResponse(productEntityStub);
        exchangeRateServiceResponseRubStub = ExchangeRate.builder()
                .currency(PriceCurrency.RUB)
                .rate(BigDecimal.ONE)
                .build();
    }

    @Test
    void getProductByIdShouldReturn404WhenNotFound() throws Exception {
        var id = controllerResponseStub.id();
        when(productServiceMock.getById(id))
                .thenThrow(new ResourceNotFoundException(ProductEntity.class, id));

        mockMvc.perform(get("/api/v1/products/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(
                        String.format("Resource ProductEntity with id = %s not found", id))
                )
                .andExpect(jsonPath("$.exception").value("ResourceNotFoundException"))
                .andExpect(jsonPath("$.source").isNotEmpty())
                .andExpect(jsonPath("$.dateTime").isNotEmpty());
    }

    @Test
    void createProductShouldReturn400WhenJakartaValidationFails() throws Exception {
        var nonValidCreateRequestStub = CreateProductRequest.builder()
                .price(BigDecimal.TEN.negate()) // negate
                .article(StringTestUtils.getStringByLength(151))
                .build();

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nonValidCreateRequestStub)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.exception").value("MethodArgumentNotValidException"))
                .andExpect(jsonPath("$.source").isNotEmpty())
                .andExpect(jsonPath("$.dateTime").isNotEmpty());
    }

    @Test
    void createProductShouldReturn500WhenUnexpectedError() throws Exception {
        var request = controllerRequestStub;

        when(productServiceMock.create(any()))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Unexpected error"))
                .andExpect(jsonPath("$.exception").value("RuntimeException"))
                .andExpect(jsonPath("$.source").isNotEmpty())
                .andExpect(jsonPath("$.dateTime").isNotEmpty());
    }

    @Test
    void createProductShouldReturn409WhenArticleNotUnique() throws Exception {
        var request = controllerRequestStub;
        var id = UUID.randomUUID();
        when(productServiceMock.create(any()))
                .thenThrow(new NotUniqueArticleException(id));

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(
                        String.format("Product with productId = %s already uses this article", id)
                ))
                .andExpect(jsonPath("$.exception").value("NotUniqueArticleException"))
                .andExpect(jsonPath("$.source").isNotEmpty())
                .andExpect(jsonPath("$.dateTime").isNotEmpty());
    }

    @Test
    void createProductShouldReturn422WhenDataIntegrityViolation() throws Exception {
        var request = controllerRequestStub;

        when(productServiceMock.create(any()))
                .thenThrow(new DataIntegrityViolationException("DB constraint failed"));

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("DB constraint failed"))
                .andExpect(jsonPath("$.exception").value("DataIntegrityViolationException"))
                .andExpect(jsonPath("$.source").isNotEmpty())
                .andExpect(jsonPath("$.dateTime").isNotEmpty());
    }


    @Test
    void createProductShouldReturn400WhenInvalidJson() throws Exception {
        var invalidJson = "{ \"name\": \"Product\" "; // обрезали кавычку, JSON сломан

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.exception").value("HttpMessageNotReadableException"))
                .andExpect(jsonPath("$.source").isNotEmpty())
                .andExpect(jsonPath("$.dateTime").isNotEmpty());
    }

    @Test
    void updateProductShouldReturn400WhenValidationFails() throws Exception {
        var id = UUID.randomUUID();
        var invalidUpdate = UpdateProductRequest.builder()
                .name("       ")
                .price(new BigDecimal("-50"))
                .article(StringTestUtils.getStringByLength(101))
                .quantity(new BigDecimal("-110.05"))
                .build();

        mockMvc.perform(patch("/api/v1/products/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdate)))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.exception").value("MethodArgumentNotValidException"))
                .andExpect(jsonPath("$.message", containsString("Name must not be blank")))
                .andExpect(jsonPath("$.message", containsString("Price must be positive")))
                .andExpect(jsonPath("$.message", containsString("Quantity must be positive or zero")))
                .andExpect(jsonPath("$.message", containsString("Article must be no longer than 100 characters")))

                .andExpect(jsonPath("$.source").isNotEmpty())
                .andExpect(jsonPath("$.dateTime").isNotEmpty());
    }

    @Test
    void createProductShouldReturn201WhenValidRequest() throws Exception {
        var expectedResponse = controllerResponseStub;

        when(productControllerConverterMock.toCommand(controllerRequestStub)).thenReturn(createCommandStub);
        when(productServiceMock.create(createCommandStub)).thenReturn(serviceResponseStub);

        var response = mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(controllerRequestStub)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        var id = objectMapper.readValue(response.getContentAsString(), UUID.class);

        assertThat(id).isEqualByComparingTo(expectedResponse.id());
    }

    @Test
    void updateProductShouldReturn200WhenValidRequest() throws Exception {
        var controllerUpdateRequest = UpdateProductRequest.builder()
                .category(Category.CLOTHING)
                .build();

        var updateCommand = UpdateProductCommandInfo.builder()
                .category(Category.CLOTHING)
                .build();

        when(productControllerConverterMock.toCommand(controllerUpdateRequest))
                .thenReturn(updateCommand);
        when(productServiceMock.update(productEntityStub.getId(), updateCommand))
                .thenReturn(serviceResponseStub);

        var response = mockMvc.perform(patch("/api/v1/products/{id}", productEntityStub.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(controllerUpdateRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        var resultId = objectMapper.readValue(response.getContentAsString(), UUID.class);
        var expectedId = productEntityStub.getId();
        assertThat(resultId).isEqualByComparingTo(expectedId);
    }

    @Test
    void getAllProductsShouldReturn200WithPage() throws Exception {
        var pageable = PageRequest.of(0, 10, Sort.by("name").ascending());

        var servicePage = new PageImpl<>(List.of(serviceResponseStub), pageable, 1);

        var expectedPageResponse =
                ProductTestDataFactory.getPageGetProductResponse(pageable, List.of(productEntityStub));

        when(productServiceMock.getAll(any(Pageable.class))).thenReturn(servicePage);
        when(exchangeRateServiceMock.getCurrentSessionExchangeRate()).thenReturn(exchangeRateServiceResponseRubStub);
        when(productControllerConverterMock.toResponse(servicePage, exchangeRateServiceResponseRubStub))
                .thenReturn(expectedPageResponse);

        mockMvc.perform(get("/api/v1/products")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "name,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(controllerResponseStub.id().toString()))
                .andExpect(jsonPath("$.content[0].name").value(controllerResponseStub.name()))
                .andExpect(jsonPath("$.content[0].currency").value(exchangeRateServiceResponseRubStub.currency().name()))
                .andExpect(jsonPath("$.totalCount").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.currentPageSize").value(1));
    }

    @Test
    void getAllProductsShouldReturnEmptyPage() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductDetailedResponse> emptyServicePage = new PageImpl<>(List.of(), pageable, 0);
        PageGetProductResponse<GetProductResponse> expectedPageResponse =
                ProductTestDataFactory.getPageGetProductResponse(pageable, List.of());

        when(productServiceMock.getAll(any(Pageable.class)))
                .thenReturn(emptyServicePage);
        when(exchangeRateServiceMock.getCurrentSessionExchangeRate())
                .thenReturn(exchangeRateServiceResponseRubStub);
        when(productControllerConverterMock.toResponse(emptyServicePage, exchangeRateServiceResponseRubStub))
                .thenReturn(expectedPageResponse);

        mockMvc.perform(get("/api/v1/products")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalCount").value(0))
                .andExpect(jsonPath("$.totalPages").value(0))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.currentPageSize").value(0));
    }

    @Test
    void deleteProductShouldReturn204WhenExists() throws Exception {
        UUID id = productEntityStub.getId();

        mockMvc.perform(delete("/api/v1/products/{id}", id))
                .andExpect(status().isNoContent());

        verify(productServiceMock).delete(id);
    }

    @Test
    void deleteProductShouldReturn404WhenNotExists() throws Exception {
        UUID id = UUID.randomUUID();

        doThrow(new ResourceNotFoundException(ProductEntity.class, id))
                .when(productServiceMock).delete(id);

        mockMvc.perform(delete("/api/v1/products/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(
                        String.format("Resource ProductEntity with id = %s not found", id))
                )
                .andExpect(jsonPath("$.exception").value("ResourceNotFoundException"));

        verify(productServiceMock).delete(id);
    }

    @Test
    void searchShouldReturn200WithPage() throws Exception {
        var pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
        var servicePage = new PageImpl<>(
                List.of(serviceResponseStub),
                pageable, 1
        );

        var expectedPageResponse =
                ProductTestDataFactory.getPageGetProductResponse(pageable, List.of(productEntityStub));

        when(productServiceMock.simpleSearch(any(SimpleSearchDto.class)))
                .thenReturn(servicePage);
        when(exchangeRateServiceMock.getCurrentSessionExchangeRate())
                .thenReturn(exchangeRateServiceResponseRubStub);
        when(productControllerConverterMock.toResponse(servicePage, exchangeRateServiceResponseRubStub))
                .thenReturn(expectedPageResponse);

        mockMvc.perform(get("/api/v1/products/search")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "name,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(controllerResponseStub.id().toString()))
                .andExpect(jsonPath("$.content[0].name").value(controllerResponseStub.name()))
                .andExpect(jsonPath("$.totalCount").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.currentPageSize").value(1));

        verify(productServiceMock).simpleSearch(any(SimpleSearchDto.class));
        verify(productControllerConverterMock).toResponse(servicePage, exchangeRateServiceResponseRubStub);
    }


    @Test
    void testSearchShouldReturnsMappedResponse() throws Exception {
        var pageableStub = PageRequest.of(0, 20, Sort.by("id").ascending());
        List<AdvancedSearchParam<?>> paramsStub = List.of(new StringParam("name", "test", FilterOperation.LIKE));

        var serviceResponsePageStub = new PageImpl<>(
                List.of(serviceResponseStub),
                pageableStub, 1
        );

        var expectedPageResponse =
                ProductTestDataFactory.getPageGetProductResponse(pageableStub, List.of(productEntityStub));

        when(productServiceMock.advancedSearch(pageableStub, paramsStub))
                .thenReturn(serviceResponsePageStub);
        when(exchangeRateServiceMock.getCurrentSessionExchangeRate())
                .thenReturn(exchangeRateServiceResponseRubStub);
        when(productControllerConverterMock.toResponse(serviceResponsePageStub, exchangeRateServiceResponseRubStub))
                .thenReturn(expectedPageResponse);

        mockMvc.perform(post("/api/v1/products/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paramsStub)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(controllerResponseStub.id().toString()))
                .andExpect(jsonPath("$.content[0].name").value(controllerResponseStub.name()))
                .andExpect(jsonPath("$.totalCount").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.pageSize").value(20))
                .andExpect(jsonPath("$.currentPageSize").value(1));

        verify(productServiceMock).advancedSearch(pageableStub, paramsStub);
        verify(productControllerConverterMock).toResponse(serviceResponsePageStub, exchangeRateServiceResponseRubStub);
    }
}
