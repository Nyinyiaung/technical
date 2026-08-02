package com.technical.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "address")
@EntityListeners(AuditingEntityListener.class)
public class Address {

    private String location;

    @Column(name = "province_id")
    private Integer provinceId;

    @Column(name = "district_id")
    private Integer districtId;

    @Column(name = "sub_district_id")
    private Integer subDistrictId;

    @Column(name = "postal_code")
    private String postalCode;

    private String alley;

    private String road;

    @Column(name = "village_no")
    private String villageNo;

    @Column(name = "address_no")
    private String addressNo;

    @Column(name = "phone")
    private String phone;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "shop_id")
    private Long shopId;

    @CreatedDate
    @Column(name = "created_time", updatable = false)
    private LocalDateTime createdTime;

    @LastModifiedDate
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
}
