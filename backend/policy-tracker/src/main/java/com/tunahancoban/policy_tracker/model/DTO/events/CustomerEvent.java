package com.tunahancoban.policy_tracker.model.DTO.events;

import com.tunahancoban.policy_tracker.model.entity.Customer;
import com.tunahancoban.policy_tracker.model.enums.EventTypes;

import java.time.LocalDateTime;

public record CustomerEvent(
        EventTypes eventType,
        String customerId,
        String fullName,
        String identityNumber,
        String email,
        String phoneNumber,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime deletedAt,
        LocalDateTime updatedAt
) {
    public static CustomerEvent from(Customer customer, EventTypes eventType) {
        return new CustomerEvent(
                eventType,
                customer.getCustomerId(),
                customer.getFullName(),
                customer.getIdentityNumber(),
                customer.getEmail(),
                customer.getPhoneNumber(),
                customer.getIsActive(),
                customer.getCreatedAt(),
                customer.getDeletedAt(),
                customer.getUpdatedAt()
        );
    }
}