package com.tunahancoban.policy_tracker.model.DTO.events;

import com.tunahancoban.policy_tracker.model.entity.Policy;
import com.tunahancoban.policy_tracker.model.enums.EventTypes;
import com.tunahancoban.policy_tracker.model.enums.InstallmentOptions;
import com.tunahancoban.policy_tracker.model.enums.PolicyStatus;
import com.tunahancoban.policy_tracker.model.enums.PolicyType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public record PolicyEvent(
        EventTypes eventType,
        String id,
        String policyId,
        String customerId,
        InstallmentOptions installment,
        PolicyStatus isActive,
        PolicyType type,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal premium,
        String responsibleUserId,
        String note,
        String previousPolicyId,
        String rootPolicyId,
        Integer renewalSequence,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt,
        Set<Integer> notifiedThresholds
) {
    public static PolicyEvent from(Policy policy, EventTypes eventType) {
        return new PolicyEvent(
                eventType,
                policy.getId(),
                policy.getPolicyId(),
                policy.getCustomerId(),
                policy.getInstallment(),
                policy.getIsActive(),
                policy.getType(),
                policy.getStartDate(),
                policy.getEndDate(),
                policy.getPremium(),
                policy.getResponsibleUserId(),
                policy.getNote(),
                policy.getPreviousPolicyId(),
                policy.getRootPolicyId(),
                policy.getRenewalSequence(),
                policy.getCreatedAt() != null ? policy.getCreatedAt() : LocalDateTime.now(),
                policy.getUpdatedAt(),
                policy.getDeletedAt(),
                policy.getNotifiedThresholds()
        );
    }
}