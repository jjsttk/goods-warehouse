package com.jjsttk.goodswarehouse.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjsttk.goodswarehouse.controller.request.CreateProductRequest;
import com.jjsttk.goodswarehouse.controller.request.UpdateProductRequest;
import com.jjsttk.goodswarehouse.controller.response.GetProductResponse;
import com.jjsttk.goodswarehouse.persistence.entity.ProductEntity;
import com.jjsttk.goodswarehouse.persistence.repository.ProductRepository;
import com.jjsttk.goodswarehouse.service.command.ProductCreateCommand;
import com.jjsttk.goodswarehouse.testutil.ProductTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.TimeZone;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    private ProductEntity entityStub;
    private CreateProductRequest createDtoStub;
    private UpdateProductRequest updateDtoStub;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        entityStub = ProductTestDataFactory.getProductEntityWithoutGeneratedId();
        createDtoStub = ProductTestDataFactory.getCreateProductRequest(entityStub);
        updateDtoStub = ProductTestDataFactory.getUpdateProductRequest(entityStub);

        productRepository.saveAndFlush(entityStub);
    }

    @Test
    void createProductShouldReturnId() throws Exception {
        productRepository.deleteAll();
        var request = post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDtoStub));

        var response = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        var id = objectMapper.readValue(response.getContentAsString(), UUID.class);

        var productFromDb = productRepository.findById(id).orElseThrow();
        assertThat(productFromDb.getId()).isEqualTo(id);
    }

    @Test
    void getProductByIdShouldReturnGetProductResponse() throws Exception {
        var productId = entityStub.getId();

        var response = mockMvc.perform(get("/api/v1/products/{id}", productId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        var result = objectMapper.readValue(response.getContentAsString(), GetProductResponse.class);
        assertThat(result.id()).isEqualTo(productId);
        assertThat(result.name()).isEqualTo(createDtoStub.getName());
        assertThat(result.quantity()).isEqualByComparingTo(createDtoStub.getQuantity());
        assertThat(result.category()).isEqualTo(createDtoStub.getCategory());
        assertThat(result.article()).isEqualTo(createDtoStub.getArticle());
        assertThat(result.price()).isEqualByComparingTo(createDtoStub.getPrice());
        assertThat(result.description()).isEqualTo(createDtoStub.getDescription());
    }

    @Test
    void updateShouldUpdateProductAndReturnId() throws Exception {
        var expectedQuantity = new BigDecimal("9999");
        var expectedDescription = "Updated test";
        entityStub.setQuantity(new BigDecimal("3301"));
        var previousTime = entityStub.getLastQuantityModified();
        updateDtoStub.setQuantity(expectedQuantity);
        updateDtoStub.setDescription(expectedDescription);

        var updateRequest = patch("/api/v1/products/{id}", entityStub.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDtoStub));

        var updateResponse = mockMvc.perform(updateRequest)
                .andExpect(status().isOk())
                .andReturn().getResponse();

        var idInResponse = objectMapper.readValue(updateResponse.getContentAsString(), UUID.class);
        var updatedProduct = productRepository.findById(idInResponse).orElseThrow();

        assertThat(updatedProduct.getId()).isEqualTo(idInResponse);
        assertThat(updatedProduct.getDescription()).isEqualTo(expectedDescription);
        assertThat(updatedProduct.getQuantity()).isEqualByComparingTo(expectedQuantity);
        assertThat(updatedProduct.getCategory()).isNotNull();
        assertThat(updatedProduct.getPrice()).isNotNull();
        assertThat(updatedProduct.getName()).isNotNull();
        assertThat(updatedProduct.getArticle()).isNotNull();
        assertThat(updatedProduct.getCreatedAt()).isNotNull();
        assertThat(updatedProduct.getLastQuantityModified()).isAfter(previousTime);
    }

    @Test
    void deleteProductShouldReturnNoContentAndRemoveFromDb() throws Exception {
        var targetId = entityStub.getId();
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/products/{id}", targetId))
                .andExpect(status().isNoContent());

        assertThat(productRepository.existsById(targetId)).isFalse();
    }

    @Test
    void getAllProductsShouldReturnGetPageProductResponse() throws Exception {
        productRepository.deleteAll();
        var products = ProductTestDataFactory.getProductsList(50);
        productRepository.saveAll(products);

        var page = 0;
        var size = 5;
        var pagesCount = products.size() / 5;
        var currentPage = 0;
        var response = mockMvc.perform(get("/api/v1/products")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        var jsonNode = objectMapper.readTree(response.getContentAsString());

        var contentNode = jsonNode.get("content");
        assertThat(contentNode.isArray()).isTrue();
        assertThat(contentNode.size()).isEqualTo(size);

        assertThat(jsonNode.get("totalCount").asInt()).isEqualTo(products.size());
        assertThat(jsonNode.get("totalPages").asInt()).isEqualTo(pagesCount);
        assertThat(jsonNode.get("currentPage").asInt()).isEqualTo(currentPage);
        assertThat(jsonNode.get("pageSize").asInt()).isEqualTo(size);
        assertThat(jsonNode.get("currentPageSize").asInt()).isEqualTo(size);
    }

    @Test
    void shouldHandleDifferentTimeZonesInResponse() throws Exception {
        OffsetDateTime testTime = OffsetDateTime.parse("2023-01-01T12:00:00+09:00"); // Tokyo time
        entityStub.setLastQuantityModified(testTime);
        productRepository.save(entityStub);

        mockMvc.perform(get("/api/v1/products/{id}", entityStub.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastQuantityModified").value("2023-01-01T12:00:00+09:00"));
    }

    @Test
    void shouldPreserveTimeZoneWhenUpdatingProduct() throws Exception {
        ProductCreateCommand updateCommand = ProductCreateCommand.builder()
                .name("Updated Product")
                .article("UPDATED123")
                .price(new BigDecimal("99.99"))
                .quantity(new BigDecimal("10"))
                .build();

        mockMvc.perform(patch("/api/v1/products/{id}", entityStub.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCommand)))
                .andExpect(status().isOk());

        ProductEntity updatedProduct = productRepository.findById(entityStub.getId()).orElseThrow();
        assertThat(updatedProduct.getLastQuantityModified()).isNotNull();
    }

    @Test
    void shouldHandleUTCtimeZoneCorrectly() throws Exception {
        OffsetDateTime utcTime = OffsetDateTime.parse("2023-01-01T12:00:00Z");
        entityStub.setLastQuantityModified(utcTime);
        productRepository.save(entityStub);

        mockMvc.perform(get("/api/v1/products/{id}", entityStub.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastQuantityModified").value("2023-01-01T12:00:00Z"));
    }

    @Test
    void shouldHandleTimeZoneConversionInJson() throws Exception {
        TimeZone originalTimeZone = TimeZone.getDefault();

        try {
            testWithTimeZone("UTC");
            testWithTimeZone("Asia/Tokyo");
            testWithTimeZone("America/New_York");
        } finally {
            TimeZone.setDefault(originalTimeZone);
        }
    }

    private void testWithTimeZone(String timeZoneId) throws Exception {
        TimeZone.setDefault(TimeZone.getTimeZone(timeZoneId));

        ProductEntity product = ProductTestDataFactory.getProductEntityWithoutGeneratedId();
        product = productRepository.save(product);

        mockMvc.perform(get("/api/v1/products/{id}", product.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastQuantityModified").exists());
    }


    @Test
    void shouldCorrectlyConvertTimeBetweenDifferentTimeZones() throws Exception {
        // Пользователь из Токио сохраняет данные
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Tokyo"));

        var tokioCreateDto = createDtoStub;
        tokioCreateDto.setName("Tokyo Product");
        tokioCreateDto.setArticle("TOKYO123");

        var createResult = mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tokioCreateDto)))
                .andExpect(status().isCreated())
                .andReturn();

        var responseContent = createResult.getResponse().getContentAsString();
        var tokyoProductId = objectMapper.readValue(responseContent, UUID.class);
        var tokyoProduct = productRepository.findById(tokyoProductId);

        // Пользователь из Америки читает данные
        TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));

        var getResult = mockMvc.perform(get("/api/v1/products/{id}", tokyoProductId))
                .andExpect(status().isOk())
                .andReturn();

        var getResponseContent = getResult.getResponse().getContentAsString();
        var nyResponse = objectMapper.readValue(getResponseContent, GetProductResponse.class);

        // Проверяем, что момент времени одинаков, несмотря на разные часовые пояса
        assertThat(nyResponse.lastQuantityModified().toInstant())
                .isEqualTo(tokyoProduct.get().getLastQuantityModified().toInstant());

        // Проверяем, что смещения разные (соответствуют часовым поясам)
        assertThat(nyResponse.lastQuantityModified().getOffset().getTotalSeconds())
                .isNotEqualTo(tokyoProduct.get().getLastQuantityModified().getOffset().getTotalSeconds());
    }
}
