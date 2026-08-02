package com.technical.entity;

import com.technical.entity.user.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "favourite")
@EntityListeners(AuditingEntityListener.class)
public class Favourite {

    @Id
    @Column(name = "favouriteid")
    private Long favouriteProductId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @CreatedDate
    @Column(name = "created_time", updatable = false)
    private LocalDateTime createdTime;
}
