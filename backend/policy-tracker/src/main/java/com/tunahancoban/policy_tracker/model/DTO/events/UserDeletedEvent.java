package com.tunahancoban.policy_tracker.model.DTO.events;

import com.tunahancoban.policy_tracker.model.entity.User;
import com.tunahancoban.policy_tracker.model.enums.Role;

import java.time.LocalDateTime;

public record UserDeletedEvent(
        String userId,
        String firstName,
        String lastName,
        String email,
        Role role,
        LocalDateTime deletedAt
) {
    public static UserDeletedEvent from(User user) {
        return new UserDeletedEvent(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole(),
                LocalDateTime.now()
        );
    }
}