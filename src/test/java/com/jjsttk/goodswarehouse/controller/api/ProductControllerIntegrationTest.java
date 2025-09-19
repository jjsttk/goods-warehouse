package com.jjsttk.goodswarehouse.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjsttk.goodswarehouse.controller.request.CreateProductRequest;
import com.jjsttk.goodswarehouse.controller.request.UpdateProductRequest;
import com.jjsttk.goodswarehouse.controller.response.GetProductResponse;
import com.jjsttk.goodswarehouse.persistence.entity.ProductEntity;
import com.jjsttk.goodswarehouse.persistence.repository.ProductRepository;
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
    void shouldHandleUTCtimeZoneCorrectly() throws Exception {
        OffsetDateTime utcTime = OffsetDateTime.parse("2023-01-01T12:00:00Z");
        entityStub.setLastQuantityModified(utcTime);
        productRepository.save(entityStub);

        mockMvc.perform(get("/api/v1/products/{id}", entityStub.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastQuantityModified").value("2023-01-01T12:00:00Z"));
    }
}
