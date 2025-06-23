package com.template.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.template.util.ValidationConstants;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuCreateRequest {
    
    @NotBlank(message = ValidationConstants.NOT_BLANK_MESSAGE)
    @Size(min = 2, max = 100, message = "Menu name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = ValidationConstants.NOT_BLANK_MESSAGE)
    @Size(min = 1, max = 200, message = "Menu path must be between 1 and 200 characters")
    private String path;

    // Optional - if not provided, will be auto-assigned to highest + 1
    @Min(value = 1, message = "Display order must be positive")
    private Integer displayOrder;

    @Size(max = 500, message = ValidationConstants.DESCRIPTION_SIZE_MESSAGE)
    private String description;

    @Builder.Default
    private Boolean isActive = true;

    @Size(max = 50, message = "Icon name must not exceed 50 characters")
    private String icon;

    private Long parentMenuId; // ID of parent menu for hierarchical structure
} 