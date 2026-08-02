package com.technical.dto;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class UpdateProductRequest {
    
    private String name;
    
    private String description;
    
    private String image;
    
    private Long categoryId;
    
    private Long shopId;
    
    private String status;
    
    @Positive(message = "Quantity must be positive")
    private Integer quantity;
}
