package com.technical.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDto {
    private Long productId;
    private String name;
    private String description;
    private String image;
    private CategoryDTO category;
    private ShopDto shop;
    private String status;
    private Integer quantity;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private String createdBy;
    private String updatedBy;
}
