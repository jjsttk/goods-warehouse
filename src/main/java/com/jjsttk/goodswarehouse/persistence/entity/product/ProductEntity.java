package com.jjsttk.goodswarehouse.persistence.entity.product;

import com.jjsttk.goodswarehouse.persistence.entity.BaseEntity;
import com.jjsttk.goodswarehouse.shared.enums.product.Category;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "product")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@EqualsAndHashCode(of = "article")
public class ProductEntity implements BaseEntity {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "article", nullable = false, unique = true)
    private String article;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private Category category;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "quantity", nullable = false)
    private BigDecimal quantity;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable;

    @Column(name = "last_quantity_modified", nullable = false)
    @Builder.Default
    private OffsetDateTime lastQuantityModified = OffsetDateTime.now();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDate createdAt;

    /**
     * Updates the product quantity and automatically tracks the modification timestamp.
     * <p>
     * This method sets the product's quantity to the specified value and updates
     * the {@code lastQuantityModified} timestamp to the current date and time,
     * but only if the quantity actually changes. This prevents unnecessary updates
     * to the modification timestamp when the quantity remains unchanged.
     *
     * @param newQuantity newQuantity to be set
     */
    public void setQuantity(BigDecimal newQuantity) {
        if (this.quantity == null || this.quantity.compareTo(newQuantity) != 0) {
            this.quantity = newQuantity;
            this.lastQuantityModified = OffsetDateTime.now();
        }
    }

    public ProductEntity(UUID id, String name, String article, String description,
                         Category category, BigDecimal price, BigDecimal quantity,
                         Boolean isAvailable, OffsetDateTime lastQuantityModified,
                         LocalDate createdAt) {
        this.id = id;
        this.name = name;
        this.article = article;
        this.description = description;
        this.category = category;
        this.isAvailable = isAvailable;
        this.createdAt = createdAt;
        this.lastQuantityModified = lastQuantityModified;
        this.setQuantity(quantity);
        this.price = price;
    }

}
