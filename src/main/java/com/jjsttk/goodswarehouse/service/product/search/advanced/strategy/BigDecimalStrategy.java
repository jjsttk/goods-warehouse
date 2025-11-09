package com.jjsttk.goodswarehouse.service.product.search.advanced.strategy;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Strategy implementation for handling {@link BigDecimal} comparisons
 * in JPA Criteria queries.
 * <p>
 * In addition to the default comparison methods from {@link Strategy},
 * this class defines a custom "like" behavior for numeric values by applying
 * a percentage-based deviation around the provided value.
 */
@Component
public class BigDecimalStrategy implements Strategy<BigDecimal> {

    /**
     * Builds a numeric "like" predicate by checking whether the
     * expression's value falls within a ±10% range of the provided value.
     *
     * @param expression the numeric field to compare
     * @param value      the base value for comparison
     * @param cb         the CriteriaBuilder instance
     * @return a Predicate that ensures the field is between
     *         -10% and +10% of the given value
     */
    @Override
    public Predicate like(Expression<BigDecimal> expression, BigDecimal value, CriteriaBuilder cb) {
        return cb.and(
                cb.lessThanOrEqualTo(expression, value.multiply(priceDeviationValue(10))),
                cb.greaterThanOrEqualTo(expression, value.multiply(priceDeviationValue(-10))));
    }

    /**
     * Calculates a deviation multiplier based on the provided percentage.
     * For example:
     * <ul>
     *   <li>percent = 10 → returns 1.1</li>
     *   <li>percent = -10 → returns 0.9</li>
     * </ul>
     *
     * @param percent the percentage to apply (positive or negative)
     * @return a {@link BigDecimal} multiplier representing the deviation
     */
    private BigDecimal priceDeviationValue(long percent) {
        return BigDecimal.ONE.add(
                BigDecimal.valueOf(percent / 100.0)
        );
    }
}
