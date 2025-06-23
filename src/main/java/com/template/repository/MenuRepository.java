package com.template.repository;

import com.template.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    
    // Find menus ordered by display order
    List<Menu> findByIsActiveTrueOrderByDisplayOrderAsc();
    
    // Find all menus ordered by display order (including inactive)
    List<Menu> findAllByOrderByDisplayOrderAsc();
    
    // Find root menus (no parent) ordered by display order
    @Query("SELECT m FROM Menu m WHERE m.parentMenu IS NULL ORDER BY m.displayOrder ASC")
    List<Menu> findRootMenusOrderedByDisplayOrder();
    
    // Find sub-menus for a parent menu ordered by display order
    @Query("SELECT m FROM Menu m WHERE m.parentMenu.id = :parentId ORDER BY m.displayOrder ASC")
    List<Menu> findSubMenusOrderedByDisplayOrder(@Param("parentId") Long parentId);
    
    // Find menu by name
    Optional<Menu> findByName(String name);
    
    // Find menus with display order greater than specified value
    @Query("SELECT m FROM Menu m WHERE m.displayOrder > :order ORDER BY m.displayOrder ASC")
    List<Menu> findMenusWithOrderGreaterThan(@Param("order") Integer order);
    
    // Update display order for menus
    @Modifying
    @Query("UPDATE Menu m SET m.displayOrder = m.displayOrder + 1 WHERE m.displayOrder >= :order")
    void incrementOrdersFrom(@Param("order") Integer order);
    
    // Get the maximum display order
    @Query("SELECT COALESCE(MAX(m.displayOrder), 0) FROM Menu m")
    Integer findMaxDisplayOrder();
    
    // Find menu with parent - useful for debugging
    @Query("SELECT m FROM Menu m LEFT JOIN FETCH m.parentMenu WHERE m.id = :id")
    Optional<Menu> findByIdWithParent(@Param("id") Long id);
} 