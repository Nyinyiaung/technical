package com.technical.controller;

import com.technical.entity.notification.Notification;
import com.technical.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable String userId) {
        List<Notification> notifications = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<Notification>> getShopNotifications(@PathVariable Long shopId) {
        List<Notification> notifications = notificationService.getShopNotifications(shopId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/user/{userId}/unread/count")
    public ResponseEntity<Long> countUnreadUserNotifications(@PathVariable String userId) {
        Long count = notificationService.countUnreadUserNotifications(userId);
        return ResponseEntity.ok(count);
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(@PathVariable Long id) {
        Notification notification = notificationService.markAsRead(id);
        return ResponseEntity.ok(notification);
    }

    @PatchMapping("/user/{userId}/read-all")
    public ResponseEntity<Void> markUserNotificationsAsRead(@PathVariable String userId) {
        notificationService.markUserNotificationsAsRead(userId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/shop/{shopId}/read-all")
    public ResponseEntity<Void> markShopNotificationsAsRead(@PathVariable Long shopId) {
        notificationService.markShopNotificationsAsRead(shopId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> clearUserNotifications(@PathVariable String userId) {
        notificationService.clearUserNotifications(userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/shop/{shopId}")
    public ResponseEntity<Void> clearShopNotifications(@PathVariable Long shopId) {
        notificationService.clearShopNotifications(shopId);
        return ResponseEntity.noContent().build();
    }
}
