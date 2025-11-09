package com.jjsttk.goodswarehouse.service.product.search.advanced.strategy;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.springframework.stereotype.Component;

/**
 * Strategy implementation for handling {@link String} comparisons
 * in JPA Criteria queries.
 * <p>
 * In addition to the default comparison methods from {@link Strategy},
 * this class provides a "like" behavior by performing a case-insensitive
 * partial match on the string field.
 */
@Component
public class StringStrategy implements Strategy<String> {

    /**
     * Builds a case-insensitive "like" predicate by checking whether
     * the field contains the provided value as a substring.
     *
     * @param expression the string field to compare
     * @param value      the substring to search for
     * @param cb         the CriteriaBuilder instance
     * @return a Predicate that performs a case-insensitive LIKE match
     */
    @Override
    public Predicate like(Expression<String> expression, String value, CriteriaBuilder cb) {
        return cb.like(cb.lower(expression), "%" + value.toLowerCase() + "%");
    }
}
