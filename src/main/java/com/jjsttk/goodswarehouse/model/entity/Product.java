package com.jjsttk.goodswarehouse.model.entity;

import com.jjsttk.goodswarehouse.model.enums.Category;
import com.jjsttk.goodswarehouse.model.listener.ProductQuantityUpdateListener;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@EntityListeners({AuditingEntityListener.class, ProductQuantityUpdateListener.class})
@Table(
        name = "products",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_products_article", columnNames = "article")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "article")
public final class Product {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "article", nullable = false)
    private Long article;

    @Column(name = "description", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private Category category;

    @Column(name = "price", nullable = false, scale = 2)
    private BigDecimal price;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    private LocalDateTime lastQuantityModified;

    @CreatedDate
    private LocalDateTime createdAt;

}
