package com.tunahancoban.policy_tracker.service;

import com.tunahancoban.policy_tracker.model.entity.Notification;
import com.tunahancoban.policy_tracker.model.entity.User;
import com.tunahancoban.policy_tracker.repository.NotificationRepository;
import com.tunahancoban.policy_tracker.repository.UserRepository;
import com.tunahancoban.policy_tracker.service.interfaces.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImp implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;

    @Override
    public Page<Notification> getNotifications(String userId, Pageable pageable) {
        Page<Notification> notificationList = notificationRepository.findNotificationsByUserId(userId, pageable);
        log.debug("Notification search completed - found {} record(s)", notificationList.getTotalElements());
        return notificationList;
    }

    @Override
    public Notification send(Notification notification) {
        Notification saved = notificationRepository.save(notification);
        log.info("Notification created and published ID: {}, Topic: {}", saved.getId(), saved.getTitle());


        Optional<User> user = userRepository.findById(saved.getUserId());
        if (user.isPresent()) {
            messagingTemplate.convertAndSendToUser(
                    user.get().getEmail(),
                    "/queue/notifications",
                    saved
            );
        } else {
            log.warn("Cannot send WS notification — user not found for id: {}", saved.getUserId());
        }

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
    public Long getUnreadCount(String userId) {

        return notificationRepository.countByUserIdAndReadIsFalse(userId);
    }

}
