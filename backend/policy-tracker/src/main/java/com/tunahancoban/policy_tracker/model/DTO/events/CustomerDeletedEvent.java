package com.tunahancoban.policy_tracker.model.DTO.events;

import com.tunahancoban.policy_tracker.model.entity.Customer;

import java.time.LocalDateTime;

public record CustomerDeletedEvent(
        String customerId,
        String firstName,
        String lastName,
        String identityNumber,
        String email,
        LocalDateTime deletedAt
) {
    public static CustomerDeletedEvent from(Customer customer) {
        return new CustomerDeletedEvent(
                customer.getCustomerId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getIdentityNumber(),
                customer.getEmail(),
                LocalDateTime.now()
        );
    }
}