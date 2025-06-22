package com.template.jquery.entity;

import com.template.entity.BaseAuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Product extends BaseAuditEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    private String name;

    @Column(length = 1000)
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Price format is invalid")
    private BigDecimal price;

    @Column(nullable = false)
    @NotBlank(message = "Category is required")
    @Size(min = 2, max = 50, message = "Category must be between 2 and 50 characters")
    private String category;

    @Column(name = "sub_category")
    @Size(max = 50, message = "Sub-category must not exceed 50 characters")
    private String subCategory;

    @Column(name = "brand")
    @Size(max = 50, message = "Brand must not exceed 50 characters")
    private String brand;

    @Column(name = "sku", unique = true)
    @Size(max = 50, message = "SKU must not exceed 50 characters")
    private String sku;

    @Column(name = "stock_quantity")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean active = true;

    @Column(name = "is_featured")
    @Builder.Default
    private Boolean featured = false;

    @Column(name = "launch_date")
    private LocalDateTime launchDate;

    @Column(name = "discontinue_date")
    private LocalDateTime discontinueDate;

    @Column(name = "weight_kg", precision = 8, scale = 3)
    @DecimalMin(value = "0.0", message = "Weight cannot be negative")
    private BigDecimal weightKg;

    @Column(name = "rating", precision = 3, scale = 2)
    @DecimalMin(value = "0.0", message = "Rating cannot be negative")
    @DecimalMax(value = "5.0", message = "Rating cannot exceed 5.0")
    private BigDecimal rating;

    @Column(name = "review_count")
    @Min(value = 0, message = "Review count cannot be negative")
    @Builder.Default
    private Integer reviewCount = 0;

    @Column(name = "tags")
    @Size(max = 500, message = "Tags must not exceed 500 characters")
    private String tags;

    // Helper methods for jQuery filtering
    public boolean isInPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        if (price == null) return false;
        if (minPrice != null && price.compareTo(minPrice) < 0) return false;
        if (maxPrice != null && price.compareTo(maxPrice) > 0) return false;
        return true;
    }

    public boolean isInStockRange(Integer minStock, Integer maxStock) {
        if (stockQuantity == null) return false;
        if (minStock != null && stockQuantity < minStock) return false;
        if (maxStock != null && stockQuantity > maxStock) return false;
        return true;
    }

    public boolean isLaunchedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        if (launchDate == null) return false;
        if (startDate != null && launchDate.isBefore(startDate)) return false;
        if (endDate != null && launchDate.isAfter(endDate)) return false;
        return true;
    }
} 