package com.jjsttk.goodswarehouse.configuration;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.jjsttk.goodswarehouse.enums.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CategoryDeserializerTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Category.class, new CategoryDeserializer());
        objectMapper.registerModule(module);
    }

    @Test
    void deserializeShouldReturnEnumForValidValueRegardlessOfCase() throws Exception {
        assertEquals(Category.ELECTRONICS, objectMapper.readValue("\"ELECTRONICS\"", Category.class));
        assertEquals(Category.ELECTRONICS, objectMapper.readValue("\"electronics\"", Category.class));
        assertEquals(Category.ELECTRONICS, objectMapper.readValue("\"Electronics\"", Category.class));

        assertEquals(Category.TOYS, objectMapper.readValue("\"toys\"", Category.class));
        assertEquals(Category.TOYS, objectMapper.readValue("\"TOYS\"", Category.class));
    }

    @Test
    void deserializeShouldThrowValidationExceptionForInvalidValue() {
        var exception = assertThrows(JsonMappingException.class, () ->
                objectMapper.readValue("\"INVALID_CATEGORY\"", Category.class)
        );

        assertTrue(exception.getMessage().contains("INVALID_CATEGORY"));
    }
}
