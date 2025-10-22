package com.jjsttk.goodswarehouse.service.product.search;

import com.jjsttk.goodswarehouse.enums.FilterOperation;
import com.jjsttk.goodswarehouse.persistence.entity.ProductEntity;
import com.jjsttk.goodswarehouse.persistence.repository.ProductRepository;
import com.jjsttk.goodswarehouse.service.product.search.advanced.param.AdvancedSearchParam;
import com.jjsttk.goodswarehouse.service.product.search.advanced.param.BigDecimalParam;
import com.jjsttk.goodswarehouse.service.product.search.advanced.param.LocalDateParam;
import com.jjsttk.goodswarehouse.service.product.search.advanced.param.StringParam;
import com.jjsttk.goodswarehouse.service.product.search.advanced.strategy.BigDecimalStrategy;
import com.jjsttk.goodswarehouse.service.product.search.advanced.strategy.LocalDateStrategy;
import com.jjsttk.goodswarehouse.service.product.search.advanced.strategy.StringStrategy;
import com.jjsttk.goodswarehouse.service.product.search.simple.SimpleSearchDto;
import com.jjsttk.goodswarehouse.testutil.ProductTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@Import({
        StringStrategy.class,
        BigDecimalStrategy.class,
        LocalDateStrategy.class,
        ProductSpecification.class
})
class ProductSpecificationIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    private static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRE_SQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRE_SQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRE_SQL_CONTAINER::getPassword);
    }

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductSpecification specification;

    private List<ProductEntity> productEntities;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        productEntities = ProductTestDataFactory.getProductsList(20);
        productRepository.saveAllAndFlush(productEntities);
    }

    // ----------------------SIMPLE SPECIFICATION TESTS----------------------------

    @Test
    @Transactional
    public void buildSimpleSpecificationShouldFilterByNameContains() {
        var product = productEntities.getLast();
        var nameToFind = "Find me";

        product.setName(nameToFind);
        productRepository.saveAndFlush(product);

        var spec1 = specification.buildSimpleSpecification(
                SimpleSearchDto.builder()
                        .name("find") //CaseInsensitive
                        .build()
        );

        var spec2 = specification.buildSimpleSpecification(
                SimpleSearchDto.builder()
                        .name(nameToFind)
                        .build()
        );

        var result1 = productRepository.findAll(spec1);
        var result2 = productRepository.findAll(spec2);

        assertThat(result1).hasSizeGreaterThanOrEqualTo(1);
        for (var p : result1) {
            assertThat(p.getName()).containsIgnoringCase("find");
        }

        assertThat(result2).hasSizeGreaterThanOrEqualTo(1);
        for (var p : result2) {
            assertThat(p.getName()).contains(nameToFind);
        }
    }

    @Test
    @Transactional
    public void buildSimpleSpecificationShouldFilterByPrice() {
        var product = productEntities.getLast();
        var priceToFind = BigDecimal.TEN;

        product.setPrice(priceToFind);
        productRepository.saveAndFlush(product);

        var spec = specification.buildSimpleSpecification(
                SimpleSearchDto.builder()
                        .price(priceToFind)
                        .build()
        );

        var result = productRepository.findAll(spec);

        assertThat(result).hasSizeGreaterThanOrEqualTo(1);
        for (var findRes : result) {
            assertThat(findRes.getPrice()).isLessThanOrEqualTo(priceToFind);
        }
    }

    @Test
    @Transactional
    public void buildSimpleSpecificationShouldFilterByQuantity() {
        var product = productEntities.getLast();
        var quantityToFind = BigDecimal.TEN;
        product.setQuantity(quantityToFind);
        productRepository.saveAndFlush(product);

        var spec = specification.buildSimpleSpecification(
                SimpleSearchDto.builder()
                        .quantity(BigDecimal.TEN)
                        .build()
        );

        var result = productRepository.findAll(spec);

        assertThat(result).hasSizeGreaterThanOrEqualTo(1);
        for (var findRes : result) {
            assertThat(findRes.getQuantity()).isGreaterThanOrEqualTo(quantityToFind);
        }
    }

    @Test
    @Transactional
    public void buildSimpleSpecificationShouldFilterByPriceAndQuantity() {
        var product = productEntities.getLast();
        var priceToFind = BigDecimal.TEN;
        var quantityToFind = BigDecimal.ONE;
        product.setPrice(priceToFind);
        product.setQuantity(quantityToFind);

        productRepository.saveAndFlush(product);

        var spec = specification.buildSimpleSpecification(
                SimpleSearchDto.builder()
                        .price(priceToFind)
                        .quantity(quantityToFind)
                        .build()
        );

        var result = productRepository.findAll(spec);

        assertThat(result).hasSizeGreaterThanOrEqualTo(1);
        for (var findRes : result) {
            assertThat(findRes.getPrice()).isLessThanOrEqualTo(priceToFind);
            assertThat(findRes.getQuantity()).isGreaterThanOrEqualTo(quantityToFind);
        }
    }

    @Test
    @Transactional
    public void buildSimpleSpecificationShouldFilterByNameAndByPriceAndByQuantity() {
        var product = productEntities.getLast();
        var priceToFind = BigDecimal.TEN;
        var quantityToFind = BigDecimal.ONE;
        var nameToFind = "Find me";

        product.setPrice(priceToFind);
        product.setQuantity(quantityToFind);
        product.setName(nameToFind);
        productRepository.saveAndFlush(product);

        var spec = specification.buildSimpleSpecification(
                SimpleSearchDto.builder()
                        .name("find")
                        .quantity(quantityToFind)
                        .price(priceToFind)
                        .build()
        );

        var result = productRepository.findAll(spec);

        assertThat(result).hasSizeGreaterThanOrEqualTo(1);
        for (var findRes : result) {
            assertThat(findRes.getName()).containsIgnoringCase("find");
            assertThat(findRes.getQuantity()).isGreaterThanOrEqualTo(quantityToFind);
            assertThat(findRes.getPrice()).isLessThanOrEqualTo(priceToFind);
        }
    }

    // --------------------------ADVANCED SPECIFICATION TESTS--------------------

    @Test
    @Transactional
    public void buildAdvancedSpecificationShouldFilterByStringEqual() {
        var product = productEntities.getLast();
        var nameToFind = "Exact Match Product";

        product.setName(nameToFind);
        productRepository.saveAndFlush(product);

        AdvancedSearchParam<?> stringParam = new StringParam("name", nameToFind, FilterOperation.EQUAL);

        var spec = specification.buildAdvancedSpecification(List.of(stringParam));
        var result = productRepository.findAll(spec);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo(nameToFind);
    }

    @Test
    @Transactional
    public void buildAdvancedSpecificationShouldFilterByStringLike() {
        var product = productEntities.getLast();
        var nameToFind = "Special Test Product";

        product.setName(nameToFind);
        productRepository.saveAndFlush(product);

        AdvancedSearchParam<?> stringParam = new StringParam("name", "Test", FilterOperation.LIKE);

        var spec = specification.buildAdvancedSpecification(List.of(stringParam));
        var result = productRepository.findAll(spec);

        assertThat(result).hasSizeGreaterThanOrEqualTo(1);
        for (var foundProduct : result) {
            assertThat(foundProduct.getName()).containsIgnoringCase("Test");
        }
    }

    @Test
    @Transactional
    public void buildAdvancedSpecificationShouldFilterByBigDecimalEqual() {
        var product = productEntities.getLast();
        var exactPrice = new BigDecimal("99.99");

        product.setPrice(exactPrice);
        productRepository.saveAndFlush(product);

        AdvancedSearchParam<?> bigDecimalParam = new BigDecimalParam("price", exactPrice, FilterOperation.EQUAL);

        var spec = specification.buildAdvancedSpecification(List.of(bigDecimalParam));
        var result = productRepository.findAll(spec);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getPrice()).isEqualByComparingTo(exactPrice);
    }

    @Test
    @Transactional
    public void buildAdvancedSpecificationShouldFilterByBigDecimalGreaterThanOrEqual() {
        var product = productEntities.getLast();
        var minPrice = new BigDecimal("50.00");

        product.setPrice(new BigDecimal("75.00"));
        productRepository.saveAndFlush(product);

        AdvancedSearchParam<?> bigDecimalParam = new BigDecimalParam(
                "price", minPrice, FilterOperation.GREATER_THAN_OR_EQUAL
        );

        var spec = specification.buildAdvancedSpecification(List.of(bigDecimalParam));
        var result = productRepository.findAll(spec);

        assertThat(result).hasSizeGreaterThanOrEqualTo(1);
        for (var foundProduct : result) {
            assertThat(foundProduct.getPrice()).isGreaterThanOrEqualTo(minPrice);
        }
    }

    @Test
    @Transactional
    public void buildAdvancedSpecificationShouldFilterByBigDecimalLessThanOrEqual() {
        var product = productEntities.getLast();
        var maxPrice = new BigDecimal("30.00");

        product.setPrice(new BigDecimal("25.00"));
        productRepository.saveAndFlush(product);

        AdvancedSearchParam<?> bigDecimalParam = new BigDecimalParam(
                "price", maxPrice, FilterOperation.LESS_THAN_OR_EQUAL
        );

        var spec = specification.buildAdvancedSpecification(List.of(bigDecimalParam));
        var result = productRepository.findAll(spec);

        assertThat(result).hasSizeGreaterThanOrEqualTo(1);
        for (var foundProduct : result) {
            assertThat(foundProduct.getPrice()).isLessThanOrEqualTo(maxPrice);
        }
    }

    @Test
    @Transactional
    public void buildAdvancedSpecificationShouldFilterByQuantityWithMultipleConditions() {
        var product = productEntities.getLast();
        var exactQuantity = BigDecimal.TEN;
        var productName = "Quantity Test Product";

        product.setQuantity(exactQuantity);
        product.setName(productName);
        productRepository.saveAndFlush(product);


        AdvancedSearchParam<?> bigDecimalParam = new BigDecimalParam("quantity", exactQuantity, FilterOperation.EQUAL);
        AdvancedSearchParam<?> stringParam = new StringParam("name", productName, FilterOperation.EQUAL);

        var spec = specification.buildAdvancedSpecification(List.of(bigDecimalParam, stringParam));
        var result = productRepository.findAll(spec);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getQuantity()).isEqualByComparingTo(exactQuantity);
        assertThat(result.getFirst().getName()).isEqualTo(productName);
    }

    @Test
    @Transactional
    public void buildAdvancedSpecificationShouldFilterByLocalDateEqual() {
        var product = productEntities.getLast();
        var exactDate = product.getCreatedAt();

        product.setCreatedAt(exactDate);
        productRepository.saveAndFlush(product);

        AdvancedSearchParam<?> localDateParam = new LocalDateParam("createdAt", exactDate, FilterOperation.EQUAL);

        var spec = specification.buildAdvancedSpecification(List.of(localDateParam));
        var result = productRepository.findAll(spec);

        assertThat(result).hasSizeGreaterThanOrEqualTo(1);
        for (var foundProduct : result) {
            assertThat(foundProduct.getCreatedAt()).isEqualTo(exactDate);
        }
    }

    @Test
    @Transactional
    public void buildAdvancedSpecificationShouldFilterByLocalDateGreaterThanOrEqual() {
        var product = productEntities.getLast();
        var minDate = java.time.LocalDate.of(2024, 1, 1);
        var productDate = java.time.LocalDate.of(2024, 2, 1);

        product.setCreatedAt(productDate);
        productRepository.saveAndFlush(product);

        AdvancedSearchParam<?> localDateParam = new LocalDateParam(
                "createdAt", minDate, FilterOperation.GREATER_THAN_OR_EQUAL
        );

        var spec = specification.buildAdvancedSpecification(List.of(localDateParam));
        var result = productRepository.findAll(spec);

        assertThat(result).hasSizeGreaterThanOrEqualTo(1);
        for (var foundProduct : result) {
            assertThat(foundProduct.getCreatedAt()).isAfterOrEqualTo(minDate);
        }
    }

    @Test
    @Transactional
    public void buildAdvancedSpecificationShouldFilterByLocalDateLessThanOrEqual() {
        var product = productEntities.getLast();
        var maxDate = product.getCreatedAt().plusYears(1);
        var productDate = product.getCreatedAt();

        product.setCreatedAt(productDate);
        productRepository.saveAndFlush(product);

        AdvancedSearchParam<?> localDateParam = new LocalDateParam(
                "createdAt", maxDate, FilterOperation.LESS_THAN_OR_EQUAL
        );

        var spec = specification.buildAdvancedSpecification(List.of(localDateParam));
        var result = productRepository.findAll(spec);

        assertThat(result).hasSizeGreaterThanOrEqualTo(1);
        for (var foundProduct : result) {
            assertThat(foundProduct.getCreatedAt()).isBeforeOrEqualTo(maxDate);
        }
    }

    @Test
    @Transactional
    public void buildAdvancedSpecificationShouldFilterWithMixedTypes() {
        var product = productEntities.getLast();
        var productName = "Mixed Filter Product";
        var productPrice = new BigDecimal("150.00");
        var productQuantity = new BigDecimal("5");
        var productDate = product.getCreatedAt();

        product.setName(productName);
        product.setPrice(productPrice);
        product.setQuantity(productQuantity);
        product.setCreatedAt(productDate);
        productRepository.saveAndFlush(product);

        AdvancedSearchParam<?> stringParam = new StringParam(
                "name", productName, FilterOperation.EQUAL
        );
        AdvancedSearchParam<?> bigDecimalParam1 = new BigDecimalParam(
                "price", productPrice, FilterOperation.EQUAL
        );
        AdvancedSearchParam<?> bigDecimalParam2 = new BigDecimalParam(
                "quantity", productQuantity, FilterOperation.EQUAL
        );
        AdvancedSearchParam<?> localDateParam = new LocalDateParam(
                "createdAt", productDate, FilterOperation.EQUAL
        );

        var spec = specification.buildAdvancedSpecification(
                List.of(stringParam, bigDecimalParam1, bigDecimalParam2, localDateParam)
        );
        var result = productRepository.findAll(spec);

        assertThat(result).hasSize(1);

        var foundProduct = result.getFirst();

        assertThat(foundProduct.getName()).isEqualTo(productName);
        assertThat(foundProduct.getPrice()).isEqualByComparingTo(productPrice);
        assertThat(foundProduct.getQuantity()).isEqualByComparingTo(productQuantity);
        assertThat(foundProduct.getCreatedAt()).isEqualTo(productDate);
    }

    @Test
    @Transactional
    public void buildAdvancedSpecificationShouldHandleNullValues() {
        AdvancedSearchParam<?> stringParam = new StringParam(
                "name", null, FilterOperation.EQUAL
        );
        AdvancedSearchParam<?> bigDecimalParam = new BigDecimalParam(
                "price", null, FilterOperation.GREATER_THAN_OR_EQUAL
        );

        var spec = specification.buildAdvancedSpecification(List.of(stringParam, bigDecimalParam));
        var result = productRepository.findAll(spec);

        // Should return all products since null params are ignored
        assertThat(result).hasSize(productEntities.size());
    }

    @Test
    @Transactional
    public void buildAdvancedSpecificationShouldReturnEmptyForNoMatches() {
        AdvancedSearchParam<?> stringParam = new StringParam(
                "name", "NonExistentProductName", FilterOperation.EQUAL
        );
        AdvancedSearchParam<?> bigDecimalParam = new BigDecimalParam(
                "price", new BigDecimal("9999.99"), FilterOperation.EQUAL
        );

        var spec = specification.buildAdvancedSpecification(List.of(stringParam, bigDecimalParam));
        var result = productRepository.findAll(spec);

        assertThat(result).isEmpty();
    }
}
