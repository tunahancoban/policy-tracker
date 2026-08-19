package com.tunahancoban.policy_tracker.model.DTO.events;

import com.tunahancoban.policy_tracker.model.entity.Customer;
import java.time.LocalDateTime;

public record CustomerUpdatedEvent(
        String customerId,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        LocalDateTime updatedAt
) {
    public static CustomerUpdatedEvent from(Customer customer) {
        return new CustomerUpdatedEvent(
                customer.getCustomerId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getPhoneNumber(),
                customer.getUpdatedAt()
        );
    }
}