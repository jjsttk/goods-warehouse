package com.jjsttk.goodswarehouse.service.scheduling;

import com.jjsttk.goodswarehouse.persistence.entity.ProductEntity;
import com.jjsttk.goodswarehouse.persistence.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Simple scheduler that periodically increases the prices of all products in the database by a configured percentage.
 * <p>
 * This component is intended for production (`@Profile("prod")`)
 * and is activated only if:
 * <ul>
 *     <li>{@code app.scheduling.enabled} = true</li>
 *     <li>{@link OptimizedProductPriceScheduler} is <strong>not</strong> present in the Spring context</li>
 * </ul>
 * </p>
 */
@Slf4j
@Profile("prod")
@ConditionalOnProperty(name = "app.scheduling.enabled", havingValue = "true")
@ConditionalOnMissingBean(OptimizedProductPriceScheduler.class)
@Component
@RequiredArgsConstructor
public class SimpleProductPriceScheduler implements ProductPriceScheduler {
    private final ProductRepository productRepository;

    /**
     * Price increase percentage, injected from application configuration.
     */
    @Value("#{new java.math.BigDecimal(\"${app.scheduling.price-increase-percentage}\")}")
    private BigDecimal increaseBy;

    /**
     * Main scheduler method.
     * <p>
     * Runs according to the schedule specified by {@code app.scheduling.period}.
     * It performs the following actions:
     * <ol>
     *     <li>Fetches all products from the database using {@link ProductRepository#findAll()}.</li>
     *     <li>Increases the price of each product by the configured percentage.</li>
     *     <li>Saves all updated products back to the database using {@link ProductRepository#saveAll}.</li>
     * </ol>
     * </p>
     */
    @Override
    @Scheduled(fixedDelayString = "${app.scheduling.period}")
    @Transactional
    public void increasePrice() {
        log.warn("\u001B[33m" + "SimpleProductPriceScheduler running..." + "\u001B[0m");

        var products = productRepository.findAll();
        products.forEach(this::updateEntityPrice);
        productRepository.saveAll(products);
    }


    private void updateEntityPrice(ProductEntity product) {
        var multiplier = BigDecimal.ONE.add(
                increaseBy.divide(BigDecimal.valueOf(100),
                        4, RoundingMode.HALF_UP)
        );
        product.setPrice(
                product.getPrice()
                        .multiply(multiplier)
                        .setScale(2, RoundingMode.HALF_UP)
        );
    }
}
