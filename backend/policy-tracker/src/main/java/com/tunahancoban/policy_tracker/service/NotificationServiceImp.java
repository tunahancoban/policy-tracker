package com.tunahancoban.policy_tracker.service;

import com.tunahancoban.policy_tracker.model.entity.Notification;
import com.tunahancoban.policy_tracker.repository.NotificationRepository;
import com.tunahancoban.policy_tracker.service.interfaces.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImp implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public Page<Notification> getNotifications(Pageable pageable) {
        Page<Notification> notificationList = notificationRepository.findAll(pageable);
        log.debug("Notification search completed - found {} record(s)", notificationList.getTotalElements());
        return notificationList;
    }

    @Override
    public Notification send(Notification notification) {
        Notification saved = notificationRepository.save(notification);
        log.info("Notification is creted and  published ID: {}, Topic: {}", saved.getId(), saved.getTitle());
        messagingTemplate.convertAndSend("/topic/notifications", saved);
        return saved;
    }

    @Override
    public void markAsRead(String notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }

    @Override
    public Long getUnreadCount() {
        return notificationRepository.countByReadIsFalse();
    }

}
