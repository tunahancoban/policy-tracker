package com.tunahancoban.policy_tracker.service.interfaces;

import com.tunahancoban.policy_tracker.model.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    Page<Notification> getNotifications(Pageable pageable);
    Notification send(Notification notification);
    void markAsRead(String notificationId);
    Long getUnreadCount();
}
