package com.template;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PdfGeneratorApplication {
    public static void main(String[] args) {
        SpringApplication.run(PdfGeneratorApplication.class, args);
    }
} 