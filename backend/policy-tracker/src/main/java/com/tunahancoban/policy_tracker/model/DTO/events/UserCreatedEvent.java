package com.tunahancoban.policy_tracker.model.DTO.events;

import com.tunahancoban.policy_tracker.model.entity.User;
import com.tunahancoban.policy_tracker.model.enums.Role;

import java.time.LocalDateTime;

public record UserCreatedEvent(
        String userId,
        String firstName,
        String lastName,
        String email,
        Role role,
        LocalDateTime createdAt
) {
    public static UserCreatedEvent from(User user) {
        return new UserCreatedEvent(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole(),
                LocalDateTime.now()
        );
    }
}