package com.jjsttk.goodswarehouse.persistence.repository;

import com.jjsttk.goodswarehouse.persistence.entity.ProductEntity;
import com.jjsttk.goodswarehouse.testutil.ProductTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class ProductRepositoryIntegrationTest {

    private ProductEntity productEntity;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productEntity = ProductTestDataFactory.getProductEntityWithoutGeneratedId();
        productRepository.saveAndFlush(productEntity);
    }

    @Test
    void lastQuantityModifiedSetOnPersist() {
        assertThat(productEntity.getLastQuantityModified()).isNotNull();
    }
}
