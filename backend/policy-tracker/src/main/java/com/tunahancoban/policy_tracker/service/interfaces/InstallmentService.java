package com.tunahancoban.policy_tracker.service.interfaces;

import com.tunahancoban.policy_tracker.model.entity.Installment;
import com.tunahancoban.policy_tracker.model.entity.Policy;
import com.tunahancoban.policy_tracker.model.enums.InstallmentOptions;
import com.tunahancoban.policy_tracker.model.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface InstallmentService {
    Page<Installment> getInstallment(String customerId, String policyId, Pageable pageable);
    void createInstallment(Policy policy, int installmentNumber);
    Installment updateInstallment(String installmentId ,PaymentStatus status);
    void deleteInstallment(String policyId);
}
