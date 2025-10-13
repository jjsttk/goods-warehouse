package com.jjsttk.goodswarehouse.service.search;

import com.jjsttk.goodswarehouse.service.search.advanced.param.AdvancedSearchParam;
import com.jjsttk.goodswarehouse.service.search.advanced.param.BigDecimalParam;
import com.jjsttk.goodswarehouse.service.search.advanced.param.LocalDateParam;
import com.jjsttk.goodswarehouse.service.search.simple.SimpleSearchDto;
import com.jjsttk.goodswarehouse.service.search.advanced.param.StringParam;
import com.jjsttk.goodswarehouse.service.search.advanced.strategy.BigDecimalStrategy;
import com.jjsttk.goodswarehouse.service.search.advanced.strategy.LocalDateStrategy;
import com.jjsttk.goodswarehouse.service.search.advanced.strategy.Strategy;
import com.jjsttk.goodswarehouse.service.search.advanced.strategy.StringStrategy;
import com.jjsttk.goodswarehouse.persistence.entity.ProductEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Builds JPA {@link Specification} instances for {@link ProductEntity}
 * based on simple or advanced search parameters.
 * <p>
 * This class acts as a factory for specifications that can be combined
 * to create dynamic database queries depending on provided filters.
 */
@Component
@RequiredArgsConstructor
public class ProductSpecification {

    private final StringStrategy stringStrategy;
    private final BigDecimalStrategy bigDecimalStrategy;
    private final LocalDateStrategy localDateStrategy;

    /**
     * Builds a specification using simple search DTO parameters.
     * <ul>
     *     <li>Filters by product name (case-insensitive LIKE)</li>
     *     <li>Filters by maximum price (≤)</li>
     *     <li>Filters by minimum quantity (≥)</li>
     * </ul>
     *
     * @param params the simple search DTO containing filter values (nullable fields are ignored)
     * @return a composed specification for filtering products
     */
    public Specification<ProductEntity> buildSimpleSpecification(SimpleSearchDto params) {
        return withNameContains(params.name())
                .and(withPriceLessOrEqualTo(params.price()))
                .and(withQuantityGreaterOrEqualTo(params.quantity()));
    }

    /**
     * Builds a specification using a list of advanced search parameters.
     * <p>
     * Each parameter is mapped to a corresponding specification strategy
     * depending on its type (String, BigDecimal, LocalDate).
     * Null or empty lists result in an unrestricted specification.
     *
     * @param params a list of advanced search parameters (nullable values are ignored)
     * @return a composed specification built by AND-ing individual specifications
     */
    public Specification<ProductEntity> buildAdvancedSpecification(List<AdvancedSearchParam<?>> params) {
        return params.stream()
                .map(this::toSpecification)
                .reduce(Specification.unrestricted(), Specification::and);
    }

    /**
     * Converts a single advanced search parameter into its corresponding specification.
     *
     * @param param the advanced search parameter
     * @return a specification that represents the parameter's condition
     */
    private Specification<ProductEntity> toSpecification(
            AdvancedSearchParam<?> param
    ) {
        return switch (param) {
            case StringParam stringParam -> applyStrategy(stringStrategy, stringParam);
            case BigDecimalParam bigDecimalParam -> applyStrategy(bigDecimalStrategy, bigDecimalParam);
            case LocalDateParam localDateTimeParam -> applyStrategy(localDateStrategy, localDateTimeParam);
        };
    }

    /**
     * Applies the given strategy to convert a search parameter into a specification.
     * If the parameter's value is {@code null}, a conjunction (always-true predicate) is returned.
     *
     * @param strategy the strategy for comparing values of type T
     * @param param    the advanced search parameter
     * @param <T>      the type of the parameter value (must be Comparable)
     * @return a specification representing the search condition
     */
    private <T extends Comparable<? super T>> Specification<ProductEntity> applyStrategy(
            Strategy<T> strategy,
            AdvancedSearchParam<T> param
    ) {
        if (param.value() == null) {
            return (root, query, cb) -> cb.conjunction();
        }

        return switch (param.operation()) {
            case EQUAL ->
                    (root, query, cb) -> strategy.equalTo(root.get(param.field()), param.value(), cb);
            case GREATER_THAN_OR_EQUAL ->
                    (root, query, cb) -> strategy.greaterThanOrEqualTo(root.get(param.field()), param.value(), cb);
            case LESS_THAN_OR_EQUAL ->
                    (root, query, cb) -> strategy.lessThanOrEqualTo(root.get(param.field()), param.value(), cb);
            case LIKE ->
                    (root, query, cb) -> strategy.like(root.get(param.field()), param.value(), cb);
        };
    }


    // --------------------------Simple search private helpers-------------------------------------

    /**
     * Creates a specification that filters by product name using a case-insensitive LIKE.
     *
     * @param mayContain the substring to search for (null ignored)
     * @return a specification for name filtering
     */
    private Specification<ProductEntity> withNameContains(String mayContain) {
        return (root, query, cb) -> mayContain == null ? cb.conjunction()
                : cb.like(cb.lower(root.get("name")), "%" + mayContain.toLowerCase() + "%");
    }

    /**
     * Creates a specification that filters by maximum product price (≤).
     *
     * @param maxPrice the maximum price (null ignored)
     * @return a specification for price filtering
     */
    private Specification<ProductEntity> withPriceLessOrEqualTo(BigDecimal maxPrice) {
        return (root, query, cb) -> maxPrice == null ? cb.conjunction()
                : cb.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

    /**
     * Creates a specification that filters by minimum product quantity (≥).
     *
     * @param minQuantity the minimum quantity (null ignored)
     * @return a specification for quantity filtering
     */
    private Specification<ProductEntity> withQuantityGreaterOrEqualTo(BigDecimal minQuantity) {
        return (root, query, cb) -> minQuantity == null ? cb.conjunction()
                : cb.greaterThanOrEqualTo(root.get("quantity"), minQuantity);
    }
}
