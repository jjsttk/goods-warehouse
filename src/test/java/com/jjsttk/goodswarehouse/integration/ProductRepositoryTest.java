package com.jjsttk.goodswarehouse.integration;

import com.jjsttk.goodswarehouse.model.entity.Product;
import com.jjsttk.goodswarehouse.repository.ProductRepository;
import com.jjsttk.goodswarehouse.testutil.ProductTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class ProductRepositoryTest {

    private Product product;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        product = ProductTestDataFactory.getProductEntityWithoutGeneratedId();
        productRepository.saveAndFlush(product);
    }

    @Test
    void lastQuantityModifiedSetOnPersist() {
        assertThat(product.getLastQuantityModified()).isNotNull();
    }
}
