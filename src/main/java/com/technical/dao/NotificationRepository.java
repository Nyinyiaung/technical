package com.technical.dao;

import com.technical.entity.notification.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    List<Notification> findByUser_FirstName(String userId);
    
    List<Notification> findByShop_ShopId(Long shopId);
    
    List<Notification> findByUser_FirstNameAndIsReadFalse(String userId);
    
    List<Notification> findByShop_ShopIdAndIsReadFalse(Long shopId);
    
    @Query("SELECT n FROM Notification n WHERE n.user.firstName = :userId AND n.isRead = false ORDER BY n.createdTime DESC")
    List<Notification> findUnreadNotificationsByUser(@Param("userId") String userId);
    
    @Query("SELECT n FROM Notification n WHERE n.shop.shopId = :shopId AND n.isRead = false ORDER BY n.createdTime DESC")
    List<Notification> findUnreadNotificationsByShop(@Param("shopId") Long shopId);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.firstName = :userId AND n.isRead = false")
    Long countUnreadNotificationsByUser(@Param("userId") String userId);
}
