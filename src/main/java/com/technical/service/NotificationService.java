package com.technical.service;

import com.technical.dao.NotificationRepository;
import com.technical.entity.notification.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public List<Notification> getUserNotifications(String userId) {
        return notificationRepository.findByUser_FirstName(userId);
    }

    public List<Notification> getShopNotifications(Long shopId) {
        return notificationRepository.findByShop_ShopId(shopId);
    }

    public Long countUnreadUserNotifications(String userId) {
        return notificationRepository.countUnreadNotificationsByUser(userId);
    }

    public Notification markAsRead(Long id) {
        return notificationRepository.findById(id)
                .map(notification -> {
                    notification.setIsRead(true);
                    return notificationRepository.save(notification);
                })
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));
    }

    public void markUserNotificationsAsRead(String userId) {
        List<Notification> unreadNotifications = notificationRepository.findByUser_FirstNameAndIsReadFalse(userId);
        unreadNotifications.forEach(notification -> notification.setIsRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }

    public void markShopNotificationsAsRead(Long shopId) {
        List<Notification> unreadNotifications = notificationRepository.findByShop_ShopIdAndIsReadFalse(shopId);
        unreadNotifications.forEach(notification -> notification.setIsRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }

    public void deleteNotification(Long id) {
        if (!notificationRepository.existsById(id)) {
            throw new RuntimeException("Notification not found with id: " + id);
        }
        notificationRepository.deleteById(id);
    }

    public void clearUserNotifications(String userId) {
        List<Notification> userNotifications = notificationRepository.findByUser_FirstName(userId);
        notificationRepository.deleteAll(userNotifications);
    }

    public void clearShopNotifications(Long shopId) {
        List<Notification> shopNotifications = notificationRepository.findByShop_ShopId(shopId);
        notificationRepository.deleteAll(shopNotifications);
    }
}
