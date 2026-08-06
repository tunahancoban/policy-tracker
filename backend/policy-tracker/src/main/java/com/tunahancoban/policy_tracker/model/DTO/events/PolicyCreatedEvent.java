package com.tunahancoban.policy_tracker.model.DTO.events;

import com.tunahancoban.policy_tracker.model.entity.Policy;
import com.tunahancoban.policy_tracker.model.enums.PolicyType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PolicyCreatedEvent(
        String policyId,
        String customerId,
        BigDecimal premium,
        PolicyType type,
        LocalDateTime createdAt
) {
    public static PolicyCreatedEvent from(Policy policy) {
        return new PolicyCreatedEvent(
                policy.getPolicyId(),
                policy.getCustomerId(),
                policy.getPremium(),
                policy.getType(),
                policy.getCreatedAt()
        );
    }
}