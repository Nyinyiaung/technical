package com.technical.entity.notification;

import com.technical.entity.constants.PlatFormType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "notification_state")
public class NotificationState {

    @Id
    @Column(name = "notificationStateId")
    private Long notificationStateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id", nullable = false)
    private Notification notification;

    @Enumerated(EnumType.STRING) // Stores "USER" or "SHOP" as a string in the DB
    @Column(name = "recipient_type", nullable = false)
    private PlatFormType recipientType;

    @Column(name = "recipient_id", nullable = false)
    private Long recipientId;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @CreatedDate
    @Column(name = "read_at", updatable = false)
    private LocalDateTime readAt;
}
