package com.technical.entity.notification;

import com.technical.entity.constants.PlatFormType;
import com.technical.entity.constants.TargetType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "notification")
@EntityListeners(AuditingEntityListener.class)
public class Notification {

    @Id
    @Column(name = "notificationId")
    private Long notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private NotificationTemplate notificationTemplate;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Enumerated(EnumType.STRING) // Stores "SYSTEM", "USER" or "SHOP" as a string in the DB
    @Column(name = "sender_type", nullable = false)
    private PlatFormType senderType;

    @Column(name = "target_id")
    private Long targetId;

    @Enumerated(EnumType.STRING) // Stores "SINGLE_USER", "SINGLE_SHOP", "ALL_USERS", "ALL_SHOPS", "SHOP_FOLLOWERS" as a string in the DB
    @Column(name = "target_type", nullable = false)
    private TargetType targetType;

    @Column(name = "action_url")
    private String actionUrl;

    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata;

    @CreatedDate
    @Column(name = "created_time", updatable = false)
    private LocalDateTime createdTime;
}
