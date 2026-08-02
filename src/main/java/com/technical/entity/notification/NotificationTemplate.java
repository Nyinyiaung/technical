package com.technical.entity.notification;

import com.technical.entity.constants.NotificationType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "notification_template")
public class NotificationTemplate {
    @Id
    @Column(name = "notificationTemplateId")
    private Long notificationId;

    @Enumerated(EnumType.STRING) // Stores "ORDER_STATUS", "APP_ANNOUNCEMENT", "SHOP_PROMOTION" as a string in the DB
    @Column(name = "type", nullable = false)
    private NotificationType type;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "body", nullable = false)
    private String body;
}
