package com.template.jquery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataTableResponse<T> {
    
    private int draw;                    // Draw counter from request
    private long recordsTotal;           // Total records without filtering
    private long recordsFiltered;        // Total records after filtering
    private List<T> data;               // Actual data
    private String error;               // Error message if any
    
    // Create response from Spring Page
    public static <T> DataTableResponse<T> of(Page<T> page, int draw, long totalRecords) {
        return DataTableResponse.<T>builder()
                .draw(draw)
                .recordsTotal(totalRecords)
                .recordsFiltered(page.getTotalElements())
                .data(page.getContent())
                .build();
    }
    
    // Create error response
    public static <T> DataTableResponse<T> error(int draw, String errorMessage) {
        return DataTableResponse.<T>builder()
                .draw(draw)
                .recordsTotal(0)
                .recordsFiltered(0)
                .data(List.of())
                .error(errorMessage)
                .build();
    }
} 