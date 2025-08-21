package com.jjsttk.goodswarehouse.configuration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.jjsttk.goodswarehouse.enums.Category;
import com.jjsttk.goodswarehouse.utils.ExceptionMessage;

import java.io.IOException;

/**
 * Custom Jackson deserializer for {@link Category} enum.
 * <p>
 * This deserializer allows case-insensitive deserialization of {@link Category} values from JSON strings.
 * For example, "electronics", "ELECTRONICS" or "Electronics" will all be mapped to {@link Category#ELECTRONICS}.
 * <p>
 * If the input string does not match any enum constant, a {@link JsonMappingException} is thrown
 * with a descriptive error message.
 * <p>
 * Usage in DTO:
 * <pre>
 * &#64;JsonDeserialize(using = CategoryDeserializer.class)
 * private Category category;
 * </pre>
 * <p>
 * This class is <strong>not</strong> a Spring bean. It is used directly by Jackson during JSON deserialization.
 */

public class CategoryDeserializer extends StdDeserializer<Category> {

    protected CategoryDeserializer() {
        super(Category.class);
    }

    /**
     * Deserializes a JSON string into a {@link Category} enum instance.
     *
     * @param p    the JSON parser
     * @param ctxt the deserialization context
     * @return the corresponding {@link Category} enum constant
     * @throws IOException           if an I/O error occurs during parsing
     * @throws JsonMappingException   if the input string does not match any enum constant
     */
    @Override
    public Category deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText();
        try {
            return Category.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw JsonMappingException.from(p, ExceptionMessage.categoryDoesntExists(value));
        }
    }
}
