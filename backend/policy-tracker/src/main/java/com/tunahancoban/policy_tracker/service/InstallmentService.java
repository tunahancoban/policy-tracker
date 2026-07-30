package com.tunahancoban.policy_tracker.service;

import com.tunahancoban.policy_tracker.model.entity.Installment;
import com.tunahancoban.policy_tracker.model.entity.Policy;
import com.tunahancoban.policy_tracker.model.enums.PaymentStatus;
import com.tunahancoban.policy_tracker.repository.InstallmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InstallmentService {
    private final InstallmentRepository installmentRepository;

    public Page<Installment> getInstallment(String customerId, String policyId, Pageable pageable){
        Installment searchCriteria = Installment.builder()
                .policyId(policyId)
                .customerId(customerId).build();

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withIgnoreCase()
                .withIgnorePaths(
                        "id",
                        "installmentNo",
                        "amount",
                        "status",
                        "paidAt",
                        "dueDate",
                        "createdAt",
                        "updatedAt")
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        Example<Installment> example = Example.of(searchCriteria, matcher);
        return installmentRepository.findAll(example,pageable);
    }

    public void createInstallment(Policy policy, int installmentNumber){
        List<Installment> installments = new ArrayList<>();
        double rawAmount = policy.getPremium() / installmentNumber;
        double installmentAmount = Math.round(rawAmount * 100.0) / 100.0;
        for(int i=0; i<installmentNumber; i++){
            Installment installment = Installment.builder()
                    .policyId(policy.getPolicyId())
                    .customerId(policy.getCustomerId())
                    .installmentNo(i+1)
                    .amount(installmentAmount)
                    .status(PaymentStatus.UNPAID)
                    .dueDate(policy.getStartDate().plusMonths(i))
                    .build();
            installments.add(installment);
        }
        installmentRepository.saveAll(installments);
    }

}
