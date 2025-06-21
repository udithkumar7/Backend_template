package com.template.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import com.template.util.ValidationConstants;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Code extends BaseAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = ValidationConstants.NOT_BLANK_MESSAGE)
    @Size(min = 2, max = 10, message = "Keycode must be between 2 and 10 characters")
    @Pattern(regexp = ValidationConstants.KEYCODE_PATTERN, message = ValidationConstants.KEYCODE_MESSAGE)
    private String keycode; // e.g., IN, KA, BLR

    @Column(nullable = false)
    @NotBlank(message = ValidationConstants.NOT_BLANK_MESSAGE)
    @Size(min = 1, max = 100, message = ValidationConstants.VALUEKEY_SIZE_MESSAGE)
    private String valuekey; // e.g., India, Karnataka, Bangalore

    @Size(min = 1, max = 50, message = ValidationConstants.CATEGORY_SIZE_MESSAGE)
    private String category;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Code parentCode; // null for country, country for state, state for city
} 