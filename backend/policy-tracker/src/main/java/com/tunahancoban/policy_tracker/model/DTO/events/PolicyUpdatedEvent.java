package com.tunahancoban.policy_tracker.model.DTO.events;

import com.tunahancoban.policy_tracker.model.entity.Policy;
import com.tunahancoban.policy_tracker.model.enums.PolicyType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PolicyUpdatedEvent(
        String policyId,
        String customerId,
        BigDecimal premium,
        PolicyType type,
        LocalDateTime updatedAt
) {
    public static PolicyUpdatedEvent from(Policy policy) {
        return new PolicyUpdatedEvent(
                policy.getPolicyId(),
                policy.getCustomerId(),
                policy.getPremium(),
                policy.getType(),
                policy.getUpdatedAt()
        );
    }
}