package com.jjsttk.goodswarehouse.persistence.repository;

import com.jjsttk.goodswarehouse.persistence.entity.ProductEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {
    Optional<ProductEntity> findByArticle(String article);

    // Helper for detail message with entityId of existing article in @UniqueArticle since we don't need full entity
    @Query("SELECT p.id FROM ProductEntity p WHERE p.article = :article")
    @NonNull Optional<UUID> findIdByArticle(@NonNull String article);

    // Select for update
    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @NonNull Optional<ProductEntity> findById(@NonNull UUID uuid);

    // Select for update
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @NonNull List<ProductEntity> findAll();
}
