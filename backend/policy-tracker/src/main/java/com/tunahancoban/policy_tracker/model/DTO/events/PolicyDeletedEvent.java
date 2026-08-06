package com.tunahancoban.policy_tracker.model.DTO.events;

import com.tunahancoban.policy_tracker.model.entity.Policy;
import com.tunahancoban.policy_tracker.model.enums.PolicyType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PolicyDeletedEvent(
        String policyId,
        String customerId,
        BigDecimal premium,
        PolicyType type,
        LocalDateTime deletedAt
) {
    public static PolicyDeletedEvent from(Policy policy) {
        return new PolicyDeletedEvent(
                policy.getPolicyId(),
                policy.getCustomerId(),
                policy.getPremium(),
                policy.getType(),
                LocalDateTime.now()
        );
    }
}