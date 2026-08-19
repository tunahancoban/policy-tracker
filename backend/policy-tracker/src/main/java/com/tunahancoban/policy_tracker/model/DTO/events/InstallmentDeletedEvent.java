package com.tunahancoban.policy_tracker.model.DTO.events;

import com.tunahancoban.policy_tracker.model.entity.Installment;
import com.tunahancoban.policy_tracker.model.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record InstallmentDeletedEvent(
        String id,
        String policyId,
        String customerId,
        Integer installmentNo,
        BigDecimal amount,
        PaymentStatus status,
        LocalDate dueDate,
        LocalDateTime deletedAt
) {
    public static InstallmentDeletedEvent from(Installment installment) {
        return new InstallmentDeletedEvent(
                installment.getId(),
                installment.getPolicyId(),
                installment.getCustomerId(),
                installment.getInstallmentNo(),
                installment.getAmount(),
                installment.getStatus(),
                installment.getDueDate(),
                LocalDateTime.now()
        );
    }
}