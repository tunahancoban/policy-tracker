package com.tunahancoban.policy_tracker.service;

import com.tunahancoban.policy_tracker.model.entity.Installment;
import com.tunahancoban.policy_tracker.model.entity.Policy;
import com.tunahancoban.policy_tracker.model.enums.PaymentStatus;
import com.tunahancoban.policy_tracker.repository.InstallmentRepository;
import com.tunahancoban.policy_tracker.service.interfaces.InstallmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstallmentServiceImp implements InstallmentService {

    private final InstallmentRepository installmentRepository;

    @Override
    public Page<Installment> getInstallment(String customerId, String policyId, Pageable pageable) {
        log.debug("Searching installments - customerId: {}, policyId: {}, page: {}", customerId, policyId, pageable);

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

        Page<Installment> result = installmentRepository.findAll(example, pageable);
        log.debug("Installment search completed - found {} record(s)", result.getTotalElements());

        return result;
    }

    @Override
    public void createInstallment(Policy policy, int installmentNumber) {
        log.info("Creating {} installment(s) for policy: {}", installmentNumber, policy.getPolicyId());

        List<Installment> installments = new ArrayList<>();

        BigDecimal countAsBigDecimal = BigDecimal.valueOf(installmentNumber);
        BigDecimal totalPremium = policy.getPremium();

        BigDecimal baseInstallmentAmount = totalPremium.divide(
                countAsBigDecimal,
                2,
                RoundingMode.DOWN
        );

        BigDecimal totalCalculated = baseInstallmentAmount.multiply(countAsBigDecimal);
        BigDecimal remainder = totalPremium.subtract(totalCalculated);

        for (int i = 0; i < installmentNumber; i++) {
            BigDecimal currentAmount = baseInstallmentAmount;
            if (i == 0) {
                currentAmount = currentAmount.add(remainder);
            }
            Installment installment = Installment.builder()
                    .policyId(policy.getPolicyId())
                    .customerId(policy.getCustomerId())
                    .installmentNo(i + 1)
                    .amount(currentAmount)
                    .status(PaymentStatus.UNPAID)
                    .dueDate(policy.getStartDate().plusMonths(i))
                    .build();
            installments.add(installment);
        }
        installmentRepository.saveAll(installments);
    }
}