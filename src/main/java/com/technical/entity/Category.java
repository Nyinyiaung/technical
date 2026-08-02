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
@Table(name = "category")
@EntityListeners(AuditingEntityListener.class)
public class Category {

    @Id
    @Column(name = "categoryid")
    private Long categoryId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String icon;

    @CreatedDate
    @Column(name = "created_time", updatable = false)
    private LocalDateTime createdTime;

    @LastModifiedDate
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;
}
