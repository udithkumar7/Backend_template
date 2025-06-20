package com.template.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SamplePdfData {
    private String title;
    private String content;
    private String author;
    private String date;
    private String footer;
    private String header;
    private String logoUrl;
    private String companyName;
    private String address;
    private String phone;
    private String email;
} 