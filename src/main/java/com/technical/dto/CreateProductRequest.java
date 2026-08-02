package com.technical.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateProductRequest {
    
    @NotBlank(message = "Product name is required")
    private String name;
    
    private String description;
    
    private String image;
    
    @NotNull(message = "Category ID is required")
    private Long categoryId;
    
    @NotNull(message = "Shop ID is required")
    private Long shopId;
    
    private String status = "ACTIVE";
    
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;
}
