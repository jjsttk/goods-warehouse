package com.jjsttk.goodswarehouse.unit.model;

import com.jjsttk.goodswarehouse.model.entity.Product;
import com.jjsttk.goodswarehouse.model.listener.ProductQuantityUpdateListener;
import com.jjsttk.goodswarehouse.testutil.ProductTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ProductTest {
    private Product product;
    private ProductQuantityUpdateListener listener;

    @BeforeEach
    void setUp() {
        product = ProductTestDataFactory.getProductEntityWithoutGeneratedId();

        listener = new ProductQuantityUpdateListener();
        listener.prePersist(product);
    }

    @Test
    void lastQuantityModifiedSetOnPrePersist() {
        product.setLastQuantityModified(null);

        listener.prePersist(product);

        assertThat(product.getLastQuantityModified()).isNotNull();
    }

    @Test
    void quantitySetToZeroIfNullValueReceivedOnPrePersist() {
        product.setQuantity(null);

        listener.prePersist(product);

        assertThat(product.getQuantity()).isEqualTo(0);
    }
}
