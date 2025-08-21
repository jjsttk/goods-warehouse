package com.jjsttk.goodswarehouse.repository;

import com.jjsttk.goodswarehouse.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    boolean existsByArticleAndIdNot(Long article, UUID id);
    Optional<Product> findByArticle(Long article);
}
