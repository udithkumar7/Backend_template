package com.template.jquery.dto;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductFilterRequest {
    
    // Basic filters
    private String name;
    private String description;
    private String category;
    private String subCategory;
    private String brand;
    private String sku;
    private Boolean active;
    private Boolean featured;
    
    // Price range filters
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    
    // Stock filters
    private Integer minStock;
    private Integer maxStock;
    
    // Date range filters
    private LocalDateTime launchDateFrom;
    private LocalDateTime launchDateTo;
    private LocalDateTime discontinueDateFrom;
    private LocalDateTime discontinueDateTo;
    
    // Rating filters
    private BigDecimal minRating;
    private BigDecimal maxRating;
    
    // Review count filters
    private Integer minReviews;
    private Integer maxReviews;
    
    // Weight filters
    private BigDecimal minWeight;
    private BigDecimal maxWeight;
    
    // Tags filter
    private String tags;
    private List<String> tagList;
    
    // Global search (searches across multiple fields)
    private String globalSearch;
    
    // Helper methods
    public boolean hasNameFilter() {
        return StringUtils.isNotBlank(name);
    }
    
    public boolean hasDescriptionFilter() {
        return StringUtils.isNotBlank(description);
    }
    
    public boolean hasCategoryFilter() {
        return StringUtils.isNotBlank(category);
    }
    
    public boolean hasSubCategoryFilter() {
        return StringUtils.isNotBlank(subCategory);
    }
    
    public boolean hasBrandFilter() {
        return StringUtils.isNotBlank(brand);
    }
    
    public boolean hasSkuFilter() {
        return StringUtils.isNotBlank(sku);
    }
    
    public boolean hasPriceRangeFilter() {
        return minPrice != null || maxPrice != null;
    }
    
    public boolean hasStockRangeFilter() {
        return minStock != null || maxStock != null;
    }
    
    public boolean hasLaunchDateRangeFilter() {
        return launchDateFrom != null || launchDateTo != null;
    }
    
    public boolean hasDiscontinueDateRangeFilter() {
        return discontinueDateFrom != null || discontinueDateTo != null;
    }
    
    public boolean hasRatingRangeFilter() {
        return minRating != null || maxRating != null;
    }
    
    public boolean hasReviewCountRangeFilter() {
        return minReviews != null || maxReviews != null;
    }
    
    public boolean hasWeightRangeFilter() {
        return minWeight != null || maxWeight != null;
    }
    
    public boolean hasTagsFilter() {
        return StringUtils.isNotBlank(tags) || (tagList != null && !tagList.isEmpty());
    }
    
    public boolean hasGlobalSearchFilter() {
        return StringUtils.isNotBlank(globalSearch);
    }
    
    public boolean hasActiveFilter() {
        return active != null;
    }
    
    public boolean hasFeaturedFilter() {
        return featured != null;
    }
    
    // Check if any filter is applied
    public boolean hasAnyFilter() {
        return hasNameFilter() || hasDescriptionFilter() || hasCategoryFilter() || 
               hasSubCategoryFilter() || hasBrandFilter() || hasSkuFilter() ||
               hasPriceRangeFilter() || hasStockRangeFilter() || hasLaunchDateRangeFilter() ||
               hasDiscontinueDateRangeFilter() || hasRatingRangeFilter() || 
               hasReviewCountRangeFilter() || hasWeightRangeFilter() || hasTagsFilter() ||
               hasGlobalSearchFilter() || hasActiveFilter() || hasFeaturedFilter();
    }
} 