package com.tunahancoban.policy_tracker.repository;

import com.tunahancoban.policy_tracker.model.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface NotificationRepository extends MongoRepository<Notification, Object> {
    Long countByUserIdAndReadIsFalse(String userId);

    Page<Notification> findNotificationsByUserId(String userId, Pageable pageable);
}

