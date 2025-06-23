package com.template.jquery.dto;

import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@Data
public class DataTableRequest {
    
    private int draw;                    // Draw counter for DataTables
    private int start;                   // Starting point of data
    private int length;                  // Number of records to fetch
    private String searchValue;          // Global search value
    private List<ColumnData> columns;    // Column data
    private List<OrderData> order;       // Order data
    
    // Convert to Spring Pageable
    public Pageable toPageable() {
        int page = start / length;
        
        if (order != null && !order.isEmpty()) {
            OrderData firstOrder = order.get(0);
            String columnName = columns.get(firstOrder.getColumn()).getData();
            Sort.Direction direction = "desc".equalsIgnoreCase(firstOrder.getDir()) 
                ? Sort.Direction.DESC 
                : Sort.Direction.ASC;
            
            return PageRequest.of(page, length, Sort.by(direction, columnName));
        }
        
        return PageRequest.of(page, length);
    }
    
    // Get search value for specific column
    public String getColumnSearchValue(String columnName) {
        if (columns == null) return null;
        
        return columns.stream()
            .filter(col -> columnName.equals(col.getData()) && col.getSearch() != null)
            .map(col -> col.getSearch().getValue())
            .filter(value -> value != null && !value.trim().isEmpty())
            .findFirst()
            .orElse(null);
    }
    
    @Data
    public static class ColumnData {
        private String data;
        private String name;
        private boolean searchable;
        private boolean orderable;
        private SearchData search;
    }
    
    @Data
    public static class SearchData {
        private String value;
        private boolean regex;
    }
    
    @Data
    public static class OrderData {
        private int column;
        private String dir;
    }
} 