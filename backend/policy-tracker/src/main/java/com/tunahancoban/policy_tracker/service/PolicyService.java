package com.tunahancoban.policy_tracker.service;

import com.tunahancoban.policy_tracker.annotation.LogActivity;
import com.tunahancoban.policy_tracker.mapper.CustomerMapper;
import com.tunahancoban.policy_tracker.mapper.PolicyMapper;
import com.tunahancoban.policy_tracker.model.DTO.request.CreatePolicyRequest;
import com.tunahancoban.policy_tracker.model.DTO.request.UpdatePolicyRequest;
import com.tunahancoban.policy_tracker.model.entity.Policy;
import com.tunahancoban.policy_tracker.model.enums.PolicyType;
import com.tunahancoban.policy_tracker.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PolicyService {
    private final PolicyRepository policyRepository;
    private final CustomerService customerService;
    private final IdGeneratorService idGeneratorService;
    private final InstallmentService installmentService;
    private final SimpMessagingTemplate messagingTemplate;
    private final PolicyMapper policyMapper;


    public Page<Policy> getPolicyWithParams(String customerId, String policyId, PolicyType type, Pageable pageable) {
        log.debug("Searching policies - customerId: {}, policyId: {}, type: {}, page: {}",
                customerId, policyId, type, pageable);

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

        Page<Policy> result = policyRepository.findAll(example, pageable);
        log.debug("Policy search completed - found {} record(s)", result.getTotalElements());

        return result;
    }

    public Policy getPolicyById(String policyId) {
        log.debug("Fetching policy by id: {}", policyId);

        if (policyRepository.findByPolicyId(policyId) == null) {
            log.warn("Policy not found - policyId: {}", policyId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Poliçe bulunamadı: " + policyId);
        }
        return policyRepository.findByPolicyId(policyId);
    }

    @Transactional
    @LogActivity(type = "POLICE", detail = "Poliçe oluşturuldu")
    public Policy createPolicy(CreatePolicyRequest request) {
        log.info("Creating policy - customerId: {}, type: {}, premium: {}",
                request.getCustomerId(), request.getType(), request.getPremium());

        //Checks customerId exist or not
        if (!customerService.existById(request.getCustomerId())) {
            log.warn("Policy creation failed - customer not found: {}", request.getCustomerId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Müşteri ID bulunamadı: " + request.getCustomerId());
        }

        //Checks start date is before end date
        if (request.getEndDate().isBefore(request.getStartDate())) {
            log.warn("Policy creation failed - end date ({}) is before start date ({})",
                    request.getEndDate(), request.getStartDate());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Poliçe bitiş günü başlangıç gününden önce olamaz.");
        }

        Policy policy = policyMapper.toEntity(request);

        policy.setPolicyId(idGeneratorService.generatePolicyId(request.getType()));
        policy.setCreatedAt(LocalDateTime.now());
        policy.setUpdatedAt(LocalDateTime.now());

        installmentService.createInstallment(policy, request.getInstallment().getValue());
        Policy savedPolicy = policyRepository.save(policy);

        messagingTemplate.convertAndSend("/topic/dashboard-summary", "REFRESH_DASHBOARD");
        log.info("Policy successfully created - policyId: {}, customerId: {}",
                savedPolicy.getPolicyId(), savedPolicy.getCustomerId());

        return savedPolicy;
    }

    @LogActivity(type = "POLICE", detail = "Poliçe silindi")
    public void deletePolicy(String policyID) {
        log.info("Deleting policy - policyId: {}", policyID);

        if (policyRepository.findByPolicyId(policyID)==null) {
            log.warn("Policy deletion failed - policy not found: {}", policyID);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This policy does not exist: " + policyID);
        }
        policyRepository.deleteByPolicyId(policyID);
        log.info("Policy successfully deleted - policyId: {}", policyID);
    }

    @LogActivity(type = "POLICE", detail = "Poliçe güncellendi")
    public Policy updatePolicy(String policyID, UpdatePolicyRequest request) {
        log.info("Updating policy - policyId: {}", policyID);

        Policy policy;
        try {
            policy = policyRepository.findByPolicyId(policyID);

        } catch (Exception e) {
            log.warn("Policy update failed - policy not found: {}", policyID);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This policy does not exist: " + policyID);
        }

        policyMapper.updateEntityFromRequest(request, policy);
        policy.setUpdatedAt(LocalDateTime.now());

        Policy updatedPolicy = policyRepository.save(policy);

        policyRepository.save(updatedPolicy);
        log.info("Policy successfully updated - policyId: {}", policyID);

        return updatedPolicy;
    }

}