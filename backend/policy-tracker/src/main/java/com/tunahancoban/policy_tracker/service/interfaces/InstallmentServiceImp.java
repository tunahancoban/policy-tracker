package com.tunahancoban.policy_tracker.service.interfaces;

import com.tunahancoban.policy_tracker.model.entity.Installment;
import com.tunahancoban.policy_tracker.model.entity.Policy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InstallmentServiceImp {
    Page<Installment> getInstallment(String customerId, String policyId, Pageable pageable);
    void createInstallment(Policy policy, int installmentNumber);

}
