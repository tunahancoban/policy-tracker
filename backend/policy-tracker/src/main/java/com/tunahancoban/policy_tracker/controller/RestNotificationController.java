package com.tunahancoban.policy_tracker.controller;

import com.tunahancoban.policy_tracker.model.entity.Notification;
import com.tunahancoban.policy_tracker.model.entity.User;
import com.tunahancoban.policy_tracker.service.interfaces.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "rest/api/notifications")
@RequiredArgsConstructor
public class RestNotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<Page<Notification>> getNotifications(
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal User user) {

        String userId = user.getId();

        Page<Notification> notificationList = notificationService.getNotifications(userId,pageable);

        return ResponseEntity.ok(notificationList);
    }

    @GetMapping(path = "/unread")
    public ResponseEntity<Long> getUnreadCount(@AuthenticationPrincipal User user){
        String userId = user.getId();

        Long unreadCount = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(unreadCount);
    }

    @PatchMapping(path = "/mark-as-read/{id}")
    public ResponseEntity<Void> markAsRead(@PathVariable String id){
        notificationService.markAsRead(id);

        return ResponseEntity.ok().build();
    }


}
