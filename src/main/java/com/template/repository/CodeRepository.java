package com.template.repository;

import com.template.entity.Code;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CodeRepository extends JpaRepository<Code, Long> {
    
    // Existing methods
    List<Code> findByParentCode(Code parentCode);
    Optional<Code> findBykeycode(String keycode);
    
    // Find codes ordered by display order
    List<Code> findByIsActiveTrueOrderByDisplayOrderAsc();
    
    // Find all codes ordered by display order (including inactive)
    List<Code> findAllByOrderByDisplayOrderAsc();
    
    // Find codes by parent ordered by display order
    @Query("SELECT c FROM Code c WHERE c.parentCode = :parent ORDER BY c.displayOrder ASC")
    List<Code> findByParentCodeOrderedByDisplayOrder(@Param("parent") Code parent);
    
    // Find root codes (no parent) ordered by display order
    @Query("SELECT c FROM Code c WHERE c.parentCode IS NULL ORDER BY c.displayOrder ASC")
    List<Code> findRootCodesOrderedByDisplayOrder();
    
    // Find codes by category ordered by display order
    @Query("SELECT c FROM Code c WHERE c.category = :category ORDER BY c.displayOrder ASC")
    List<Code> findByCategoryOrderedByDisplayOrder(@Param("category") String category);
    
    // Find codes with display order greater than specified value
    @Query("SELECT c FROM Code c WHERE c.displayOrder > :order ORDER BY c.displayOrder ASC")
    List<Code> findCodesWithOrderGreaterThan(@Param("order") Integer order);
    
    // Update display order for codes
    @Modifying
    @Query("UPDATE Code c SET c.displayOrder = c.displayOrder + 1 WHERE c.displayOrder >= :order")
    void incrementOrdersFrom(@Param("order") Integer order);
    
    // Get the maximum display order
    @Query("SELECT COALESCE(MAX(c.displayOrder), 0) FROM Code c")
    Integer findMaxDisplayOrder();
    
    // Find codes by parent and category
    @Query("SELECT c FROM Code c WHERE c.parentCode = :parent AND c.category = :category ORDER BY c.displayOrder ASC")
    List<Code> findByParentCodeAndCategoryOrderedByDisplayOrder(@Param("parent") Code parent, @Param("category") String category);
} 