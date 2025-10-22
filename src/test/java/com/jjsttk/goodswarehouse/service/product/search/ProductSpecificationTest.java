package com.jjsttk.goodswarehouse.service.product.search;

import com.jjsttk.goodswarehouse.enums.FilterOperation;
import com.jjsttk.goodswarehouse.persistence.entity.ProductEntity;
import com.jjsttk.goodswarehouse.service.product.search.advanced.param.AdvancedSearchParam;
import com.jjsttk.goodswarehouse.service.product.search.advanced.param.BigDecimalParam;
import com.jjsttk.goodswarehouse.service.product.search.advanced.param.LocalDateParam;
import com.jjsttk.goodswarehouse.service.product.search.simple.SimpleSearchDto;
import com.jjsttk.goodswarehouse.service.product.search.advanced.param.StringParam;
import com.jjsttk.goodswarehouse.service.product.search.advanced.strategy.BigDecimalStrategy;
import com.jjsttk.goodswarehouse.service.product.search.advanced.strategy.LocalDateStrategy;
import com.jjsttk.goodswarehouse.service.product.search.advanced.strategy.StringStrategy;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductSpecificationTest {

    @Mock
    private StringStrategy stringStrategy;

    @Mock
    private BigDecimalStrategy bigDecimalStrategy;

    @Mock
    private LocalDateStrategy localDateStrategy;

    @InjectMocks
    private ProductSpecification sut;

    @Mock
    private Root<ProductEntity> root;

    @Mock
    private CriteriaQuery<ProductEntity> query;

    @Mock
    private CriteriaBuilder cbMock;

    // ------------------ SimpleSearchDto ------------------

    @Test
    void testBuildSimpleSpecificationWithAllFields() {
        var dto = new SimpleSearchDto("product", BigDecimal.valueOf(100), BigDecimal.valueOf(10), 0, 20);
        var spec = sut.buildSimpleSpecification(dto);

        assertDoesNotThrow(() -> spec.toPredicate(root, query, cbMock));
    }

    // ------------------ AdvancedSearchParam: StringParam ------------------

    @Test
    void testBuildAdvancedSpecificationWithStringParam() {
        var param = new StringParam("name", "test", FilterOperation.LIKE);
        List<AdvancedSearchParam<?>> params = List.of(param);

        when(stringStrategy.like(any(), eq("test"), any())).thenReturn(mock(Predicate.class));

        var spec = sut.buildAdvancedSpecification(params);

        var predicate = spec.toPredicate(root, query, cbMock);
        assertNotNull(predicate);

        verify(stringStrategy).like(any(), eq("test"), any());
    }

    @Test
    void testApplyStrategyWhenValueIsNull() {
        var param = new StringParam("name", null, FilterOperation.EQUAL);

        var spec = sut.buildAdvancedSpecification(List.of(param));

        when(cbMock.conjunction()).thenReturn(mock(Predicate.class));

        var predicate = spec.toPredicate(root, query, cbMock);

        assertNotNull(predicate);
        verify(cbMock).conjunction();
    }

    @Test
    void testBuildAdvancedSpecificationWithEqualOperation() {
        var param = new StringParam("name", "value", FilterOperation.EQUAL);
        List<AdvancedSearchParam<?>> params = List.of(param);

        when(stringStrategy.equalTo(any(), eq("value"), any()))
                .thenReturn(mock(Predicate.class));

        var spec = sut.buildAdvancedSpecification(params);
        var predicate = spec.toPredicate(root, query, cbMock);

        assertNotNull(predicate);
        verify(stringStrategy).equalTo(any(), eq("value"), any());
    }


    // ------------------ AdvancedSearchParam: BigDecimalParam ------------------

    @Test
    void testBuildAdvancedSpecificationWithBigDecimalParam() {
        var param = new BigDecimalParam("price", BigDecimal.valueOf(50), FilterOperation.LESS_THAN_OR_EQUAL);
        List<AdvancedSearchParam<?>> params = List.of(param);

        when(bigDecimalStrategy.lessThanOrEqualTo(any(), eq(BigDecimal.valueOf(50)), any()))
                .thenReturn(mock(Predicate.class));

        var spec = sut.buildAdvancedSpecification(params);

        var predicate = spec.toPredicate(root, query, cbMock);
        assertNotNull(predicate);

        verify(bigDecimalStrategy).lessThanOrEqualTo(any(), eq(BigDecimal.valueOf(50)), any());
    }

    // ------------------ AdvancedSearchParam: LocalDateParam ------------------

    @Test
    void testBuildAdvancedSpecificationWithLocalDateParam() {
        var date = LocalDate.of(2025, 10, 11);
        var param = new LocalDateParam("createdAt", date, FilterOperation.GREATER_THAN_OR_EQUAL);
        List<AdvancedSearchParam<?>> params = List.of(param);

        when(localDateStrategy.greaterThanOrEqualTo(any(), eq(date), any()))
                .thenReturn(mock(Predicate.class));

        var spec = sut.buildAdvancedSpecification(params);

        var predicate = spec.toPredicate(root, query, cbMock);
        assertNotNull(predicate);

        verify(localDateStrategy).greaterThanOrEqualTo(any(), eq(date), any());
    }
}
