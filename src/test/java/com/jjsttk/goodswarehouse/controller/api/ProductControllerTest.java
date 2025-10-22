package com.jjsttk.goodswarehouse.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjsttk.goodswarehouse.controller.request.CreateProductRequest;
import com.jjsttk.goodswarehouse.controller.request.UpdateProductRequest;
import com.jjsttk.goodswarehouse.controller.response.GetPageProductResponse;
import com.jjsttk.goodswarehouse.controller.response.GetProductResponse;
import com.jjsttk.goodswarehouse.enums.Category;
import com.jjsttk.goodswarehouse.enums.FilterOperation;
import com.jjsttk.goodswarehouse.enums.PriceCurrency;
import com.jjsttk.goodswarehouse.exception.NotUniqueArticleException;
import com.jjsttk.goodswarehouse.exception.ResourceNotFoundException;
import com.jjsttk.goodswarehouse.exception.handler.GlobalExceptionHandler;
import com.jjsttk.goodswarehouse.mapper.ProductConverter;
import com.jjsttk.goodswarehouse.persistence.entity.ProductEntity;
import com.jjsttk.goodswarehouse.service.exchange.ExchangeService;
import com.jjsttk.goodswarehouse.service.exchange.response.ExchangeServiceResponse;
import com.jjsttk.goodswarehouse.service.product.ProductService;
import com.jjsttk.goodswarehouse.service.product.command.ProductCreateCommand;
import com.jjsttk.goodswarehouse.service.product.command.ProductUpdateCommand;
import com.jjsttk.goodswarehouse.service.product.response.ProductServiceResponse;
import com.jjsttk.goodswarehouse.service.product.search.advanced.param.AdvancedSearchParam;
import com.jjsttk.goodswarehouse.service.product.search.advanced.param.StringParam;
import com.jjsttk.goodswarehouse.service.product.search.simple.SimpleSearchDto;
import com.jjsttk.goodswarehouse.testutil.ProductTestDataFactory;
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
    private ProductConverter converterMock;

    @Mock
    private ProductService productServiceMock;

    @Mock
    private ExchangeService exchangeServiceMock;

    @InjectMocks
    private ProductControllerImpl sut;

    private CreateProductRequest controllerRequestStub;
    private UpdateProductRequest controllerUpdateRequestStub;
    private GetProductResponse controllerResponseStub;

    private ProductCreateCommand createCommandStub;
    private ProductUpdateCommand updateCommandStub;
    private ProductServiceResponse serviceResponseStub;

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
        controllerUpdateRequestStub = ProductTestDataFactory.getUpdateProductRequest(productEntityStub);
        controllerResponseStub = ProductTestDataFactory.getGetProductResponse(productEntityStub);

        createCommandStub = ProductTestDataFactory.getProductCreateCommand(productEntityStub);
        updateCommandStub = ProductTestDataFactory.getProductUpdateCommand(productEntityStub);
        serviceResponseStub = ProductTestDataFactory.getProductServiceResponse(productEntityStub);
    }

    @Test
    void getProductByIdShouldReturn404WhenNotFound() throws Exception {
        var id = controllerResponseStub.id();
        when(productServiceMock.getById(id))
                .thenThrow(new ResourceNotFoundException(id));

        mockMvc.perform(get("/api/v1/products/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(String.format("Resource with id = %s not found", id)))
                .andExpect(jsonPath("$.exception").value("ResourceNotFoundException"))
                .andExpect(jsonPath("$.source").isNotEmpty())
                .andExpect(jsonPath("$.dateTime").isNotEmpty());
    }

    @Test
    void createProductShouldReturn400WhenJakartaValidationFails() throws Exception {
        var request = controllerRequestStub;

        when(productServiceMock.create(any()))
                .thenThrow(new jakarta.validation.ValidationException("Jakarta validation failed"));

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Jakarta validation failed"))
                .andExpect(jsonPath("$.exception").value("ValidationException"))
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
                        String.format("Product with id %s already uses this article", id)
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
        var invalidUpdate = controllerUpdateRequestStub;
        invalidUpdate.setName("       ");
        invalidUpdate.setPrice(new BigDecimal("-50"));
        invalidUpdate.setArticle(ProductTestDataFactory.getStringByLength(101));
        invalidUpdate.setQuantity(new BigDecimal("-110.05"));

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

        when(converterMock.mapToServiceCommand(controllerRequestStub)).thenReturn(createCommandStub);
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
        controllerUpdateRequestStub.setCategory(Category.CLOTHING);

        when(converterMock.mapToServiceCommand(controllerUpdateRequestStub))
                .thenReturn(updateCommandStub);
        when(productServiceMock.update(updateCommandStub, productEntityStub.getId()))
                .thenReturn(serviceResponseStub);

        var response = mockMvc.perform(patch("/api/v1/products/{id}", productEntityStub.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(controllerUpdateRequestStub)))
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

        var exchangeResponse = List.of(
                ExchangeServiceResponse.builder()
                        .price(serviceResponseStub.price())
                        .currency(PriceCurrency.RUB)
                        .build()
        );

        var expectedPageResponse =
                ProductTestDataFactory.getGetPageProductResponse(pageable, List.of(productEntityStub));

        when(productServiceMock.getAll(any(Pageable.class))).thenReturn(servicePage);
        when(exchangeServiceMock.exchange(List.of(serviceResponseStub.price()))).thenReturn(exchangeResponse);
        when(converterMock.mapToControllerResponse(servicePage, exchangeResponse))
                .thenReturn(expectedPageResponse);

        mockMvc.perform(get("/api/v1/products")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "name,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(controllerResponseStub.id().toString()))
                .andExpect(jsonPath("$.content[0].name").value(controllerResponseStub.name()))
                .andExpect(jsonPath("$.content[0].currency").value(PriceCurrency.RUB.name()))
                .andExpect(jsonPath("$.totalCount").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.currentPageSize").value(1));
    }

    @Test
    void getAllProductsShouldReturnEmptyPage() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductServiceResponse> emptyServicePage = new PageImpl<>(List.of(), pageable, 0);
        GetPageProductResponse<GetProductResponse> expectedPageResponse =
                ProductTestDataFactory.getGetPageProductResponse(pageable, List.of());

        when(productServiceMock.getAll(any(Pageable.class))).thenReturn(emptyServicePage);
        when(exchangeServiceMock.exchange(List.of())).thenReturn(List.of());
        when(converterMock.mapToControllerResponse(emptyServicePage, List.of())).thenReturn(expectedPageResponse);

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

        doThrow(new ResourceNotFoundException(id))
                .when(productServiceMock).delete(id);

        mockMvc.perform(delete("/api/v1/products/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(String.format("Resource with id = %s not found", id)))
                .andExpect(jsonPath("$.exception").value("ResourceNotFoundException"));

        verify(productServiceMock).delete(id);
    }

    @Test
    void searchShouldReturn200WithPage() throws Exception {
        var pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
        var servicePage = new PageImpl<>(
                List.of(serviceResponseStub),
                pageable, 1);
        var expectedPageResponse =
                ProductTestDataFactory.getGetPageProductResponse(pageable, List.of(productEntityStub));

        var exchangeResponse = List.of(
                ExchangeServiceResponse.builder()
                        .price(serviceResponseStub.price())
                        .currency(PriceCurrency.RUB)
                        .build()
        );

        when(productServiceMock.simpleSearch(any(SimpleSearchDto.class))).thenReturn(servicePage);
        when(exchangeServiceMock.exchange(List.of(serviceResponseStub.price()))).thenReturn(exchangeResponse);
        when(converterMock.mapToControllerResponse(servicePage, exchangeResponse)).thenReturn(expectedPageResponse);

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
        verify(converterMock).mapToControllerResponse(servicePage, exchangeResponse);
    }


    @Test
    void testSearchShouldReturnsMappedResponse() throws Exception {
        var pageable = PageRequest.of(0, 20, Sort.by("id").ascending());
        List<AdvancedSearchParam<?>> params = List.of(new StringParam("name", "test", FilterOperation.LIKE));

        var servicePage = new PageImpl<>(
                List.of(serviceResponseStub),
                pageable, 1);
        var expectedPageResponse =
                ProductTestDataFactory.getGetPageProductResponse(pageable, List.of(productEntityStub));



        var exchangeResponse = List.of(
                ExchangeServiceResponse.builder()
                        .price(serviceResponseStub.price())
                        .currency(PriceCurrency.RUB)
                        .build()
        );

        when(productServiceMock.advancedSearch(pageable, params)).thenReturn(servicePage);
        when(exchangeServiceMock.exchange(List.of(serviceResponseStub.price()))).thenReturn(exchangeResponse);
        when(converterMock.mapToControllerResponse(servicePage, exchangeResponse)).thenReturn(expectedPageResponse);


        mockMvc.perform(post("/api/v1/products/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(controllerResponseStub.id().toString()))
                .andExpect(jsonPath("$.content[0].name").value(controllerResponseStub.name()))
                .andExpect(jsonPath("$.totalCount").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.pageSize").value(20))
                .andExpect(jsonPath("$.currentPageSize").value(1));

        verify(productServiceMock).advancedSearch(pageable, params);
        verify(converterMock).mapToControllerResponse(servicePage, exchangeResponse);
    }
}
