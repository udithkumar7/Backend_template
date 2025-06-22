package com.template.jquery.repository;

import com.template.jquery.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Basic finders
    Optional<Product> findBySku(String sku);
    List<Product> findByCategory(String category);
    List<Product> findByCategoryAndSubCategory(String category, String subCategory);
    List<Product> findByBrand(String brand);
    List<Product> findByActiveTrue();
    List<Product> findByFeaturedTrue();
    
    // Price range queries
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    List<Product> findByPriceGreaterThanEqual(BigDecimal minPrice);
    List<Product> findByPriceLessThanEqual(BigDecimal maxPrice);
    
    // Stock queries
    List<Product> findByStockQuantityGreaterThan(Integer minStock);
    List<Product> findByStockQuantityLessThan(Integer maxStock);
    List<Product> findByStockQuantityBetween(Integer minStock, Integer maxStock);
    
    // Date range queries
    List<Product> findByLaunchDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Product> findByLaunchDateAfter(LocalDateTime date);
    List<Product> findByLaunchDateBefore(LocalDateTime date);
    
    // Rating queries
    List<Product> findByRatingGreaterThanEqual(BigDecimal minRating);
    List<Product> findByRatingBetween(BigDecimal minRating, BigDecimal maxRating);
    
    // Text search queries
    @Query("SELECT p FROM Product p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.category) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.brand) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.tags) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Product> findByGlobalSearch(@Param("searchTerm") String searchTerm);
    
    // Complex search with multiple criteria
    @Query("SELECT p FROM Product p WHERE " +
           "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:category IS NULL OR LOWER(p.category) = LOWER(:category)) AND " +
           "(:brand IS NULL OR LOWER(p.brand) = LOWER(:brand)) AND " +
           "(:active IS NULL OR p.active = :active) AND " +
           "(:featured IS NULL OR p.featured = :featured) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
           "(:minStock IS NULL OR p.stockQuantity >= :minStock) AND " +
           "(:maxStock IS NULL OR p.stockQuantity <= :maxStock)")
    List<Product> findWithMultipleFilters(
            @Param("name") String name,
            @Param("category") String category,
            @Param("brand") String brand,
            @Param("active") Boolean active,
            @Param("featured") Boolean featured,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("minStock") Integer minStock,
            @Param("maxStock") Integer maxStock
    );
    
    // Count queries for statistics
    long countByCategory(String category);
    long countByBrand(String brand);
    long countByActiveTrue();
    long countByFeaturedTrue();
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.stockQuantity <= :threshold")
    long countLowStockProducts(@Param("threshold") Integer threshold);
    
    // Aggregation queries for analytics
    @Query("SELECT AVG(p.price) FROM Product p WHERE p.active = true")
    BigDecimal getAveragePrice();
    
    @Query("SELECT SUM(p.stockQuantity) FROM Product p WHERE p.active = true")
    Long getTotalStock();
    
    @Query("SELECT COUNT(DISTINCT p.category) FROM Product p WHERE p.active = true")
    Long getActiveCategoryCount();
    
    @Query("SELECT COUNT(DISTINCT p.brand) FROM Product p WHERE p.active = true")
    Long getActiveBrandCount();
} 