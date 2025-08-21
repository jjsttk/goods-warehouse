package com.jjsttk.goodswarehouse.persistence.entity;

import com.jjsttk.goodswarehouse.testutil.ProductTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ProductEntityListenerTest {
    private ProductEntity entity;
    private ProductEntityListener listener;

    @BeforeEach
    void setUp() {
        entity = ProductTestDataFactory.getProductEntityWithoutGeneratedId();

        listener = new ProductEntityListener();
        listener.prePersist(entity);
    }

    @Test
    void lastQuantityModifiedSetOnPrePersist() {
        entity.setLastQuantityModified(null);

        listener.prePersist(entity);

        assertThat(entity.getLastQuantityModified()).isNotNull();
    }

    @Test
    void quantitySetToZeroIfNullValueReceivedOnPrePersist() {
        entity.setQuantity(null);

        listener.prePersist(entity);

        assertThat(entity.getQuantity()).isEqualByComparingTo(String.valueOf(0));
        assertThat(entity.getLastQuantityModified()).isNotNull();
    }
}
