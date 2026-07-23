package com.tunahancoban.policy_tracker.service;

import com.tunahancoban.policy_tracker.annotation.LogActivity;
import com.tunahancoban.policy_tracker.model.DTO.request.CreatePolicyRequest;
import com.tunahancoban.policy_tracker.model.entity.Installment;
import com.tunahancoban.policy_tracker.model.entity.Policy;
import com.tunahancoban.policy_tracker.model.enums.PaymentStatus;
import com.tunahancoban.policy_tracker.model.enums.PolicyType;
import com.tunahancoban.policy_tracker.repository.InstallmentRepository;
import com.tunahancoban.policy_tracker.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PolicyService {
    private final PolicyRepository policyRepository;
    private final CustomerService customerService;
    private final IdGeneratorService idGeneratorService;
    private final InstallmentRepository installmentRepository;

    public List<Policy> getPolicyWithParams(String customerId, String policyId, PolicyType type){
        //We are creating a search criteria here then we are searching this in db
        Policy searchCriteria = Policy.builder()
                .policyId(policyId)
                .customerId(customerId)
                .type(type).build();

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<Policy> example = Example.of(searchCriteria, matcher);

        List<Policy> policyList = policyRepository.findAll(example);
        return policyRepository.findAll(example);
    }
    @Transactional
    @LogActivity(type = "POLICE", detail = "Poliçe oluşturuldu")
    public Policy createPolicy(CreatePolicyRequest request){
        //Checks customerId exist or not
        if (!customerService.existById(request.getCustomerId())){
            throw new RuntimeException("Müşteri ID bulunamadı: " + request.getCustomerId());
        }

        //Checks start date is before end date
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new RuntimeException("Poliçe bitiş günü başlangıç gününden önce olamaz.");
        }

        //If everything ok save policy
        Policy policy = Policy.builder()
                .policyId(idGeneratorService.generatePolicyId(request.getType()))
                .customerId(request.getCustomerId())
                .type(request.getType())
                .note(request.getNote())
                .installment(request.getInstallmentNumber())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .premium(request.getPremium())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now()).build();
        createInstallment(policy, request.getInstallmentNumber().getValue());
        return policyRepository.save(policy);
    }

    public void createInstallment(Policy policy, int installmentNumber){
        List<Installment> installments = new ArrayList<>();
        double rawAmount = policy.getPremium() / installmentNumber;
        double installmentAmount = Math.round(rawAmount * 100.0) / 100.0;
        for(int i=0; i<installmentNumber; i++){
            Installment installment = Installment.builder()
                    .policyId(policy.getPolicyId())
                    .installmentNo(i+1)
                    .amount(installmentAmount)
                    .status(PaymentStatus.UNPAID)
                    .dueDate(policy.getStartDate().plusMonths(i))
                    .build();
            installments.add(installment);
        }
        installmentRepository.saveAll(installments);
    }

    @LogActivity(type = "POLICE", detail = "Poliçe silindi")
    public void deletePolicy(String policyID){
        if(!policyRepository.existsByPolicyId(policyID)) {
            throw new RuntimeException("This policy does not exist: " + policyID);
        }
        policyRepository.deleteByPolicyId(policyID);
    }

    @LogActivity(type = "POLICE", detail = "Poliçe güncellendi")
    public Policy updatePolicy(String policyID, Map<String, Object> updates){
        if(!policyRepository.existsByPolicyId(policyID)) {
            throw new RuntimeException("This policy does not exist: " + policyID);
        }

        Policy policy = policyRepository.findByPolicyId(policyID);
        Policy.PolicyBuilder policyBuilder = policy.toBuilder();

        updates.forEach(( key,  value) -> {
           switch (key){
               case "customerId":
                    policyBuilder.customerId((String) value);
                   break;
               case "type":
                   policyBuilder.type(PolicyType.valueOf((String)value));
                   break;
               case "startDate":
                   policyBuilder.startDate(LocalDate.parse((String) value));
                   break;
               case "endDate":
                   policyBuilder.endDate(LocalDate.parse((String) value));
                   break;
               case "premium":
                   policyBuilder.premium(((Number) value).doubleValue());
                   break;
               default:
                   break;
           }
        });
        policyBuilder.updatedAt(LocalDateTime.now());
        Policy updatedPolicy = policyBuilder.build();
        policyRepository.save(updatedPolicy);
        return updatedPolicy;
    }

}
