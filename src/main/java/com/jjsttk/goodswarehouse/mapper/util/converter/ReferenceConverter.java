package com.jjsttk.goodswarehouse.mapper.util.converter;

import com.jjsttk.goodswarehouse.persistence.entity.BaseEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * A utility class for handling reference entity mappings.
 * <p>
 * This class provides methods for converting entity identifiers (IDs)
 * into entity instances using the {@link EntityManager}. It also allows
 * conversion of entity collections into ID lists.
 * </p>
 */
@Component
@RequiredArgsConstructor
public class ReferenceConverter {

    /**
     * The JPA EntityManager instance used for entity lookups.
     * Injected automatically by Spring.
     */
    private final EntityManager entityManager;

    /**
     * Converts an ID to an entity of the specified type using the {@link EntityManager}.
     * <p>
     * If the given ID is {@code null}, this method returns {@code null}.
     * Otherwise, it retrieves the entity from the database based on the provided ID.
     * </p>
     *
     * @param id          the ID of the entity to retrieve.
     * @param entityClass the class type of the entity to return.
     * @param <T>         the type of the entity, which must extend {@link BaseEntity}.
     * @return the entity corresponding to the given ID, or {@code null} if the ID is {@code null}.
     */
    public <T extends BaseEntity> T toEntity(Long id, Class<T> entityClass) {
        return id == null ? null : entityManager.getReference(entityClass, id);
    }

    /**
     * Converts an ID to an entity of the specified type using the {@link EntityManager}.
     * <p>
     * If the given ID is {@code null}, this method returns {@code null}.
     * Otherwise, it retrieves the entity from the database based on the provided ID.
     * </p>
     *
     * @param id          the ID of the entity to retrieve.
     * @param entityClass the class type of the entity to return.
     * @param <T>         the type of the entity, which must extend {@link BaseEntity}.
     * @return the entity corresponding to the given ID, or {@code null} if the ID is {@code null}.
     */
    public <T extends BaseEntity> T toEntity(UUID id, Class<T> entityClass) {
        return id == null ? null : entityManager.getReference(entityClass, id);
    }
}
