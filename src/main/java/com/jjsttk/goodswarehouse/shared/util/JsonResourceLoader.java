package com.jjsttk.goodswarehouse.shared.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjsttk.goodswarehouse.exception.service.util.ResourceLoadingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * Utility component for loading and parsing JSON objects from classpath resources.
 * Uses Jackson {@link ObjectMapper} for JSON deserialization.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JsonResourceLoader {
    private final ObjectMapper objectMapper;
    private final ResourceLoader resourceLoader;

    /**
     * Loads and deserializes a JSON object from a classpath resource.
     *
     * @param <T>      the type of object to deserialize
     * @param fileName the name of the JSON file in classpath (e.g., "data/rates.json")
     * @param type     the class type to deserialize into
     * @return the deserialized object
     * @throws ResourceLoadingException if the resource cannot be loaded or parsed
     * @throws IllegalArgumentException if fileName is null or empty
     */
    public <T> @NonNull T loadObject(@NonNull String fileName, @NonNull Class<T> type) {
        try {
            final String location = "classpath:" + fileName;
            final Resource resource = resourceLoader.getResource(location);
            log.debug("Loading object from: {}", fileName);

            final T result = objectMapper.readValue(resource.getInputStream(), type);
            log.debug("Successfully loaded object from: {}", fileName);
            return result;

        } catch (Exception e) {
            log.debug(e.getMessage());
            throw new ResourceLoadingException(e.getMessage(), e);
        }
    }
}

