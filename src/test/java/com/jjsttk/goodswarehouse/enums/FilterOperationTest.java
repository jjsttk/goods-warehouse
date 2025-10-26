package com.jjsttk.goodswarehouse.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilterOperationTest {
    @Test
    void testFromStringWithSymbols() {
        assertEquals(FilterOperation.EQUAL, FilterOperation.fromString("=="));
        assertEquals(FilterOperation.GREATER_THAN_OR_EQUAL, FilterOperation.fromString(">="));
        assertEquals(FilterOperation.LESS_THAN_OR_EQUAL, FilterOperation.fromString("<="));
        assertEquals(FilterOperation.LIKE, FilterOperation.fromString("~"));
    }

    @Test
    void testFromStringWithAliases() {
        assertEquals(FilterOperation.EQUAL, FilterOperation.fromString("eq"));
        assertEquals(FilterOperation.EQUAL, FilterOperation.fromString("equal"));
        assertEquals(FilterOperation.GREATER_THAN_OR_EQUAL, FilterOperation.fromString("gtOrEq"));
        assertEquals(FilterOperation.GREATER_THAN_OR_EQUAL, FilterOperation.fromString("greaterThanOrEqual"));
        assertEquals(FilterOperation.LESS_THAN_OR_EQUAL, FilterOperation.fromString("ltOrEq"));
        assertEquals(FilterOperation.LESS_THAN_OR_EQUAL, FilterOperation.fromString("lessThanOrEqual"));
        assertEquals(FilterOperation.LIKE, FilterOperation.fromString("like"));
        assertEquals(FilterOperation.LIKE, FilterOperation.fromString("iLike"));
    }

    @Test
    void testFromStringCaseInsensitive() {
        assertEquals(FilterOperation.EQUAL, FilterOperation.fromString("Eq"));
        assertEquals(FilterOperation.LIKE, FilterOperation.fromString("ILIKE"));
        assertEquals(FilterOperation.GREATER_THAN_OR_EQUAL, FilterOperation.fromString("GREATERTHANOREQUAL"));
    }

    @Test
    void testFromStringWithNullValue() {
        assertNull(FilterOperation.fromString(null));
    }

    @Test
    void testFromStringWithUnknownValue() {
        assertThrows(IllegalArgumentException.class, () -> FilterOperation.fromString("unknown"));
    }
}
