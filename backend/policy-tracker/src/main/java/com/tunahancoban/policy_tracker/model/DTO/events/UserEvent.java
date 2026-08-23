package com.tunahancoban.policy_tracker.model.DTO.events;

import com.tunahancoban.policy_tracker.model.entity.User;
import com.tunahancoban.policy_tracker.model.enums.EventTypes;
import com.tunahancoban.policy_tracker.model.enums.Role;

import java.time.LocalDateTime;

public record UserEvent(
        EventTypes eventType,
        String id,
        String firstName,
        String lastName,
        String email,
        Boolean isActive,
        LocalDateTime deletedAt,
        String password,
        Role role,
        LocalDateTime createdAt
) {
    public static UserEvent from(User user, EventTypes eventType) {
        return new UserEvent(
                eventType,
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getIsActive(),
                user.getDeletedAt(),
                user.getPassword(),
                user.getRole(),
                LocalDateTime.now()
        );
    }
}