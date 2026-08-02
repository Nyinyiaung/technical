package com.technical.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShopDto {
    private Long shopId;
    private String name;
    private String image;
    private String phone;
    private String email;
    private String remark;
    private String location;
    private Integer provinceId;
    private Integer districtId;
    private Integer subDistrictId;
    private String postalCode;
    private String alley;
    private String road;
    private String villageNo;
    private String addressNo;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private String createdBy;
    private String updatedBy;
}
