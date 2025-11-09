package com.jjsttk.goodswarehouse.service.product.search.advanced.strategy;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;

/**
 * A generic strategy interface for building comparison predicates
 * used in JPA Criteria queries.
 *
 * @param <T> the type of the field being compared;
 *            must implement {@link Comparable}
 */
public interface Strategy<T extends Comparable<? super T>> {

    /**
     * Builds a predicate that checks whether the given expression
     * is greater than or equal to the provided value.
     *
     * @param expression the entity field to compare
     * @param value the value to compare against
     * @param cb the CriteriaBuilder instance
     * @return a Predicate representing the "greaterThanOrEqualTo" condition
     */
    default Predicate greaterThanOrEqualTo(
            Expression<T> expression, T value, CriteriaBuilder cb
    ) {
        return cb.greaterThanOrEqualTo(expression, value);
    }

    /**
     * Builds a predicate that checks whether the given expression
     * is less than or equal to the provided value.
     *
     * @param expression the entity field to compare
     * @param value the value to compare against
     * @param cb the CriteriaBuilder instance
     * @return a Predicate representing the "lessThanOrEqualTo" condition
     */
    default Predicate lessThanOrEqualTo(
            Expression<T> expression, T value, CriteriaBuilder cb
    ) {
        return cb.lessThanOrEqualTo(expression, value);
    }

    /**
     * Builds a predicate that checks whether the given expression
     * is equal to the provided value.
     *
     * @param expression the entity field to compare
     * @param value the value to compare against
     * @param cb the CriteriaBuilder instance
     * @return a Predicate representing the "equal" condition
     */
    default Predicate equalTo(
            Expression<T> expression, T value, CriteriaBuilder cb
    ) {
        return cb.equal(expression, value);
    }

    /**
     * Builds a predicate for performing a "like" operation
     * on the given expression and value.
     * Implementations define how case sensitivity or pattern
     * matching should be handled.
     *
     * @param expression the entity field to compare
     * @param value the value to match
     * @param cb the CriteriaBuilder instance
     * @return a Predicate representing the "like" condition
     */
    Predicate like(Expression<T> expression, T value, CriteriaBuilder cb);
}
