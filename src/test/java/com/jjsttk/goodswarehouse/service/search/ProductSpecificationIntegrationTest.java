package com.jjsttk.goodswarehouse.service.search;

import com.jjsttk.goodswarehouse.enums.FilterOperation;
import com.jjsttk.goodswarehouse.persistence.entity.ProductEntity;
import com.jjsttk.goodswarehouse.persistence.repository.ProductRepository;
import com.jjsttk.goodswarehouse.service.search.advanced.param.AdvancedSearchParam;
import com.jjsttk.goodswarehouse.service.search.simple.SimpleSearchDto;
import com.jjsttk.goodswarehouse.service.search.advanced.param.StringParam;
import com.jjsttk.goodswarehouse.service.search.advanced.strategy.BigDecimalStrategy;
import com.jjsttk.goodswarehouse.service.search.advanced.strategy.LocalDateStrategy;
import com.jjsttk.goodswarehouse.service.search.advanced.strategy.StringStrategy;
import com.jjsttk.goodswarehouse.testutil.ProductTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({ProductSpecification.class, StringStrategy.class,
        BigDecimalStrategy.class, LocalDateStrategy.class})
class ProductSpecificationIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductSpecification productSpecification;

    private List<ProductEntity> entities;

    @BeforeEach
    void setup() {
        productRepository.deleteAll();
        entities = ProductTestDataFactory.getProductsList(10);
    }

    // Simple: Name contains + Price less or equal + Quantity greater or equal
    @Test
    void testSimpleSpecificationWithAllFilterFields() {
        var expectedEntityFirst = entities.getFirst();
        expectedEntityFirst.setName("ContainsThisName");
        expectedEntityFirst.setPrice(new BigDecimal("999.99"));
        expectedEntityFirst.setQuantity(new BigDecimal("10"));

        var expectedEntitySecond = entities.getLast();
        expectedEntitySecond.setName("ProductContainsThisNameToo");
        expectedEntitySecond.setPrice(new BigDecimal("500.01"));
        expectedEntitySecond.setQuantity(new BigDecimal("1"));

        productRepository.saveAllAndFlush(entities);


        var simpleSearchDto = SimpleSearchDto.builder()
                .name("this")
                .price(new BigDecimal("1000"))
                .quantity(BigDecimal.ONE)
                .page(0)
                .size(10)
                .build();

        var spec = productSpecification.buildSimpleSpecification(simpleSearchDto);

        var result = productRepository.findAll(spec, PageRequest.of(simpleSearchDto.page(), simpleSearchDto.size()));

        assertThat(result).hasSize(2);
        assertThat(result.getContent())
                .extracting(ProductEntity::getName)
                .containsExactlyInAnyOrder(expectedEntityFirst.getName(), expectedEntitySecond.getName());
        assertThat(result.getContent())
                .extracting(ProductEntity::getPrice)
                .containsExactlyInAnyOrder(expectedEntityFirst.getPrice(), expectedEntitySecond.getPrice());
        assertThat(result.getContent())
                .extracting(ProductEntity::getQuantity)
                .containsExactlyInAnyOrder(expectedEntityFirst.getQuantity(), expectedEntitySecond.getQuantity());
    }

    @Test
    void testAdvancedSpecificationByNameLike() {
        var expectedEntityFirst = entities.getFirst();
        expectedEntityFirst.setName("ContainsThisName");

        var expectedEntitySecond = entities.getLast();
        expectedEntitySecond.setName("ProductContainsThisNameToo");

        productRepository.saveAllAndFlush(entities);


        var param = StringParam.builder()
                .field("name")
                .value("tHiS")
                .operation(FilterOperation.LIKE)
                .build();
        List<AdvancedSearchParam<?>> params = List.of(param);

        var spec = productSpecification.buildAdvancedSpecification(params);


        var result = productRepository.findAll(spec, PageRequest.of(0, 10));

        assertThat(result).hasSize(2);
        assertThat(result.getContent())
                .extracting(ProductEntity::getName)
                .containsExactlyInAnyOrder(expectedEntityFirst.getName(), expectedEntitySecond.getName());
    }

    @Test
    void testAdvancedSpecificationWithEmptyParamsListReturnsNonFilteredEntityList() {
        productRepository.saveAllAndFlush(entities);

        var spec = productSpecification.buildAdvancedSpecification(Collections.emptyList());

        var result = productRepository.findAll(spec, PageRequest.of(0, 10));

        assertThat(result).hasSize(entities.size());
    }
}
