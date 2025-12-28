package com.jjsttk.goodswarehouse.mapper.util.mapstruct;

import com.jjsttk.goodswarehouse.persistence.entity.BaseEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.TargetType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

/**
 * A utility class for handling reference entity mappings.
 * <p>
 * This class provides methods for converting entity identifiers (IDs)
 * into entity instances using the {@link EntityManager}. It also allows
 * conversion of entity collections into ID lists.
 * </p>
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING
)
public abstract class MapstructReferenceMapper {

    /**
     * The JPA EntityManager instance used for entity lookups.
     * Injected automatically by Spring.
     */
    @Autowired
    private EntityManager entityManager;

    /**
     * Retrieves a managed entity by its UUID identifier.
     * <p>
     * This method is designed to be used by MapStruct during mapping operations
     * when converting UUID identifiers to entity references. It delegates to
     * {@link EntityManager#find(Class, Object)} for the actual lookup.
     * </p>
     *
     * <p><b>Example usage in MapStruct mapper:</b></p>
     * <pre>{@code
     * // In a MapStruct mapper interface:
     * @Mapping(target = "category", source = "categoryId")
     * // MapStruct will automatically call:
     * // toEntity(categoryId, CategoryEntity.class)
     * }</pre>
     *
     * @param <T>         the type of entity to retrieve, must extend {@link BaseEntity}
     * @param id          the UUID identifier of the entity to retrieve;
     *                    if {@code null}, returns {@code null}
     * @param entityClass the target entity class, automatically resolved by MapStruct
     *                    via the {@link TargetType} annotation
     * @return the managed entity instance if found, {@code null} if ID is {@code null}
     *         or if no entity exists with the given ID
     *
     * @throws EntityNotFoundException if the entity does not exist
     *         (only when {@link EntityManager#find(Class, Object)} is configured to throw)
     */
    public <T extends BaseEntity> T toEntity(UUID id, @TargetType Class<T> entityClass) {
        return id == null ? null : entityManager.getReference(entityClass, id);
    }

    /**
     * Retrieves a managed entity by its Long identifier.
     * <p>
     * This method provides the same functionality as {@link #toEntity(UUID, Class)}
     * but for entities using Long primary keys. It's commonly used for entities
     * with auto-increment database IDs.
     * </p>
     *
     * <p><b>Example usage in MapStruct mapper:</b></p>
     * <pre>{@code
     * // For entities with Long IDs like Customer:
     * @Mapping(target = "customer", source = "customerId")
     * // MapStruct will automatically call:
     * // toEntity(customerId, CustomerEntity.class)
     * }</pre>
     *
     * @param <T>         the type of entity to retrieve, must extend {@link BaseEntity}
     * @param id          the Long identifier of the entity to retrieve;
     *                    if {@code null}, returns {@code null}
     * @param entityClass the target entity class, automatically resolved by MapStruct
     *                    via the {@link TargetType} annotation
     * @return the managed entity instance if found, {@code null} if ID is {@code null}
     *         or if no entity exists with the given ID
     *
     * @throws EntityNotFoundException if the entity does not exist
     *         (only when {@link EntityManager#find(Class, Object)} is configured to throw)
     */
    public <T extends BaseEntity> T toEntity(Long id, @TargetType Class<T> entityClass) {
        return id == null ? null : entityManager.getReference(entityClass, id);
    }
}
