package com.jjsttk.goodswarehouse.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjsttk.goodswarehouse.dto.request.CreateProductRequestDto;
import com.jjsttk.goodswarehouse.dto.request.UpdateProductRequestDto;
import com.jjsttk.goodswarehouse.dto.response.ProductResponseDto;
import com.jjsttk.goodswarehouse.repository.ProductRepository;
import com.jjsttk.goodswarehouse.testutil.ProductTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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

    private CreateProductRequestDto createDto;
    private UpdateProductRequestDto updateDto;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        var product = ProductTestDataFactory.getProductEntityWithoutGeneratedId();
        createDto = ProductTestDataFactory.getCreateProductRequestDto(product);
        updateDto = ProductTestDataFactory.getUpdateProductRequestDto(product);
    }

    @Test
    void createProduct_shouldReturnCreatedAndPersisted() throws Exception {
        var request = MockMvcRequestBuilders.post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto));

        var response = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        var jsonNode = objectMapper.readTree(response.getContentAsString());
        var id = UUID.fromString(jsonNode.get("id").asText());

        assertThat(jsonNode.get("name").asText()).isEqualTo(createDto.getName());
        assertThat(jsonNode.get("article").asLong()).isEqualTo(createDto.getArticle());

        var productFromDb = productRepository.findById(id).orElseThrow();
        assertThat(productFromDb.getName()).isEqualTo(createDto.getName());
        assertThat(productFromDb.getArticle()).isEqualTo(createDto.getArticle());
        assertThat(productFromDb.getPrice()).isEqualByComparingTo(createDto.getPrice());
    }

    @Test
    void getProductById_shouldReturnProduct() throws Exception {
        var json = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        var created = objectMapper.readValue(json, ProductResponseDto.class);

        var response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/{id}", created.id()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        var node = objectMapper.readTree(response.getContentAsString());
        assertThat(node.get("id").asText()).isEqualTo(created.id().toString());
        assertThat(node.get("name").asText()).isEqualTo(created.name());
    }

    @Test
    void updateProduct_shouldReturnUpdatedAndPersisted() throws Exception {
        var json = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        var created = objectMapper.readValue(json, ProductResponseDto.class);

        var updateRequest = MockMvcRequestBuilders.patch("/api/v1/products/{id}", created.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto));

        var updateResponse = mockMvc.perform(updateRequest)
                .andExpect(status().isOk())
                .andReturn().getResponse();

        var updatedNode = objectMapper.readTree(updateResponse.getContentAsString());
        assertThat(updatedNode.get("name").asText()).isEqualTo(updateDto.getName());
        assertThat(updatedNode.get("category").asText()).isEqualTo(updateDto.getCategory());

        var updatedProduct = productRepository.findById(created.id()).orElseThrow();
        assertThat(updatedProduct.getName()).isEqualTo(updateDto.getName());
        assertThat(updatedProduct.getCategory().name()).isEqualTo(updateDto.getCategory());
    }

    @Test
    void deleteProduct_shouldReturnNoContentAndRemoveFromDb() throws Exception {
        var json = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        var created = objectMapper.readValue(json, ProductResponseDto.class);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/products/{id}", created.id()))
                .andExpect(status().isNoContent());

        var exists = productRepository.existsById(created.id());
        assertThat(exists).isFalse();
    }

    @Test
    void getAllProducts_shouldReturnList() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated());

        var response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products"))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        var listNode = objectMapper.readTree(response.getContentAsString());
        assertThat(listNode.isArray()).isTrue();
        assertThat(listNode.size()).isEqualTo(1);

        var productFromDb = productRepository.findAll().getFirst();
        assertThat(productFromDb.getName()).isEqualTo(createDto.getName());
    }
}