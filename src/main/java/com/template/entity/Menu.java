package com.template.entity;

import jakarta.persistence.*;
import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import com.template.util.ValidationConstants;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Menu extends BaseAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = ValidationConstants.NOT_BLANK_MESSAGE)
    @Size(min = 2, max = 100, message = "Menu name must be between 2 and 100 characters")
    private String name;

    @Column(nullable = false)
    @NotBlank(message = ValidationConstants.NOT_BLANK_MESSAGE)
    @Size(min = 1, max = 200, message = "Menu path must be between 1 and 200 characters")
    private String path;

    @Column(name = "display_order", nullable = false)
    @NotNull(message = ValidationConstants.NOT_NULL_MESSAGE)
    @Min(value = 0, message = "Display order must be non-negative")
    @Builder.Default
    private Integer displayOrder = 0;

    @Column(length = 500)
    @Size(max = 500, message = ValidationConstants.DESCRIPTION_SIZE_MESSAGE)
    private String description;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "icon")
    @Size(max = 50, message = "Icon name must not exceed 50 characters")
    private String icon; // For UI icons (e.g., "dashboard", "users", "settings")

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_menu_id")
    private Menu parentMenu; // For hierarchical menus (main menu -> sub menu)
} 