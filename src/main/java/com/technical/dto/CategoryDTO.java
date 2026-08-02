package com.technical.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryDTO implements Serializable {
    private Long categoryId;
    private String name;
    private String icon;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private String createdBy;
    private String updatedBy;
}
