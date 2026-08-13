package com.tunahancoban.policy_tracker.controller;

import com.tunahancoban.policy_tracker.model.entity.Notification;
import com.tunahancoban.policy_tracker.service.interfaces.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "rest/api/notification")
@RequiredArgsConstructor
public class RestNotificationController {

    private final NotificationService notificationService;

    @GetMapping(path = "/get-notifications")
    public ResponseEntity<Page<Notification>> getNotifications(@PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC)
                                               Pageable pageable){

        Page<Notification> notificationList = notificationService.getNotifications(pageable);

        return ResponseEntity.ok(notificationList);
    }

    @GetMapping(path = "/get-unread")
    public ResponseEntity<Long> getUnreadCount(){
        Long unreadCount = notificationService.getUnreadCount();

        return ResponseEntity.ok(unreadCount);
    }

    /**
     *
     * THIS LOGIC COMPLETELY WRONG FIX THIS
     *
     */
    @PatchMapping(path = "/mark-as-read/{id}")
    public ResponseEntity<Void> markAsRead(@PathVariable String id){
        notificationService.markAsRead(id);

        return ResponseEntity.ok().build();
    }


}
