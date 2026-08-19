package com.tunahancoban.policy_tracker.model.DTO.events;

import com.tunahancoban.policy_tracker.model.entity.Customer;
import java.time.LocalDateTime;

public record CustomerCreatedEvent(
        String customerId,
        String firstName,
        String lastName,
        String identityNumber,
        String email,
        String phoneNumber,
        LocalDateTime createdAt
) {
    public static CustomerCreatedEvent from(Customer customer) {
        return new CustomerCreatedEvent(
                customer.getCustomerId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getIdentityNumber(),
                customer.getEmail(),
                customer.getPhoneNumber(),
                customer.getCreatedAt()
        );
    }
}