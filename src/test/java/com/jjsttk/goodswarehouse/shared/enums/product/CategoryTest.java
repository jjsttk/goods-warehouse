package com.jjsttk.goodswarehouse.shared.enums.product;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CategoryTest {

    @Test
    void testFromValueWithValidValues() {
        assertEquals(Category.ELECTRONICS, Category.fromValue("ELECTRONICS"));
        assertEquals(Category.FOOD, Category.fromValue("food"));
        assertEquals(Category.CLOTHING, Category.fromValue("Clothing"));
        assertEquals(Category.TOYS, Category.fromValue("toys"));
        assertEquals(Category.BOOKS, Category.fromValue("Books"));
        assertEquals(Category.FURNITURE, Category.fromValue("furniture"));
        assertEquals(Category.BEAUTY, Category.fromValue("Beauty"));
        assertEquals(Category.SPORTS, Category.fromValue("sports"));
        assertEquals(Category.OFFICE, Category.fromValue("office"));
        assertEquals(Category.PETS, Category.fromValue("Pets"));
    }

    @Test
    void testFromValueWithNullValue() {
        assertNull(Category.fromValue(null));
    }

    @Test
    void testFromValueWithInvalidValue() {
        assertThrows(IllegalArgumentException.class, () -> Category.fromValue("unknown"));
    }
}
