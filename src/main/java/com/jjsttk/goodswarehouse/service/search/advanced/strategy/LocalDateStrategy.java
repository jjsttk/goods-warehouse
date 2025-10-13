package com.jjsttk.goodswarehouse.service.search.advanced.strategy;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Strategy implementation for handling {@link LocalDate} comparisons
 * in JPA Criteria queries.
 * <p>
 * In addition to the default comparison methods from {@link Strategy},
 * this class defines a custom "like" behavior by evaluating whether
 * a date falls within a small range around the given value.
 */
@Component
public class LocalDateStrategy implements Strategy<LocalDate> {

    /**
     * Builds a date-based "like" predicate by checking whether the field
     * falls within a ±3 day range of the provided date.
     *
     * @param expression the date field to compare
     * @param value      the reference date for matching
     * @param cb         the CriteriaBuilder instance
     * @return a Predicate that ensures the field is between
     *         {@code value.minusDays(3)} and {@code value.plusDays(3)}
     */
    @Override
    public Predicate like(Expression<LocalDate> expression, LocalDate value, CriteriaBuilder cb) {
        return cb.between(expression, value.minusDays(3), value.plusDays(3));
    }
}
