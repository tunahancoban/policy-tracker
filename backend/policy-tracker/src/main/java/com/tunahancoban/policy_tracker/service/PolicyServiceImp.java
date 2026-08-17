package com.tunahancoban.policy_tracker.service;

import com.tunahancoban.policy_tracker.annotation.LogActivity;
import com.tunahancoban.policy_tracker.mapper.PolicyMapper;
import com.tunahancoban.policy_tracker.model.DTO.events.PolicyCreatedEvent;
import com.tunahancoban.policy_tracker.model.DTO.events.PolicyDeletedEvent;
import com.tunahancoban.policy_tracker.model.DTO.events.PolicyUpdatedEvent;
import com.tunahancoban.policy_tracker.model.DTO.request.CreatePolicyRequest;
import com.tunahancoban.policy_tracker.model.DTO.request.RenewPolicyRequest;
import com.tunahancoban.policy_tracker.model.DTO.request.UpdatePolicyRequest;
import com.tunahancoban.policy_tracker.model.entity.Policy;
import com.tunahancoban.policy_tracker.model.enums.PolicyStatus;
import com.tunahancoban.policy_tracker.model.enums.PolicyType;
import com.tunahancoban.policy_tracker.repository.PolicyRepository;
import com.tunahancoban.policy_tracker.service.interfaces.CustomerService;
import com.tunahancoban.policy_tracker.service.interfaces.IdGeneratorService;
import com.tunahancoban.policy_tracker.service.interfaces.InstallmentService;
import com.tunahancoban.policy_tracker.service.interfaces.PolicyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PolicyServiceImp implements PolicyService {
    private final PolicyRepository policyRepository;
    private final CustomerService customerService;
    private final IdGeneratorService idGeneratorService;
    private final InstallmentService installmentService;
    private final ApplicationEventPublisher eventPublisher;
    private final PolicyMapper policyMapper;


    @Override
    public Page<Policy> getPolicyWithParams(String customerId, String policyId, PolicyType type, String responsibleUserId, PolicyStatus active, Pageable pageable) {
        log.debug("Searching policies - customerId: {}, policyId: {}, type: {}, page: {}",
                customerId, policyId, type, pageable);

        //We are creating a search criteria here then we are searching this in db
        Policy searchCriteria = Policy.builder()
                .policyId(policyId)
                .customerId(customerId)
                .active(active)
                .responsibleUserId(responsibleUserId)
                .type(type).build();

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.EXACT)
                .withIgnorePaths("renewalSequence", "createdAt", "updatedAt", "premium",
                        "note", "startDate", "endDate", "installment",
                        "previousPolicyId", "rootPolicyId" , "deletedAt" , "renewalSequence" , "notifiedThresholds");
        Example<Policy> example = Example.of(searchCriteria, matcher);

        Page<Policy> result = policyRepository.findAll(example, pageable);
        log.debug("Policy search completed - found {} record(s)", result.getTotalElements());

        return result;
    }

    @Override
    public Policy getPolicyById(String policyId) {
        log.debug("Fetching policy by id: {}", policyId);

        return policyRepository.getPolicyByPolicyId(policyId).orElseThrow(()->
        {log.warn("Policy not found - policyId: {}", policyId);
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Poliçe bulunamadı: " + policyId);
        });
    }

    @Transactional
    @LogActivity(type = "POLICE", detail = "'Poliçe oluşturuldu ID: ' + #result.policyId")
    @Override
    public Policy createPolicy(CreatePolicyRequest request) {
        log.info("Renewing policy - customerId: {}, type: {}, premium: {}",
                request.getCustomerId(), request.getType(), request.getPremium());

        if (!customerService.existById(request.getCustomerId())) {
            log.warn("Policy create failed - customer not found: {}", request.getCustomerId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Müşteri ID bulunamadı: " + request.getCustomerId());
        }


        Policy policy = policyMapper.toEntity(request);

        policy.setPolicyId(idGeneratorService.generatePolicyId(request.getType()));
        policy.setPreviousPolicyId(null); //Policy is created now so it does not have any prev policy
        policy.setRootPolicyId(policy.getPolicyId()); // Root PolicyID itself

        installmentService.createInstallment(policy, request.getInstallment().getValue());
        Policy savedPolicy = policyRepository.save(policy);

        eventPublisher.publishEvent(PolicyCreatedEvent.from(savedPolicy));

        log.info("Policy successfully created - policyId: {}, customerId: {}", savedPolicy.getPolicyId(), savedPolicy.getCustomerId());
        return savedPolicy;
    }

    @Transactional
    @LogActivity(type = "POLICE", detail = "'Poliçe silindi. ID: ' + #id")
    @Override
    public void deletePolicy(String policyID) {
        log.info("Deleting policy - policyId: {}", policyID);

        Policy policy = policyRepository.getPolicyByPolicyId(policyID).orElseThrow(() ->{
            log.warn("Policy deletion failed - policy not found: {}", policyID);
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "This policy does not exist: " + policyID);
        });
        //policyRepository.deleteByPolicyId(policyID);

        policy.setActive(PolicyStatus.PASSIVE);
        policy.setDeletedAt(LocalDateTime.now());
        installmentService.deleteInstallment(policyID);

        policyRepository.save(policy);

        eventPublisher.publishEvent(PolicyDeletedEvent.from(policy));

        log.info("Policy successfully deleted - policyId: {}", policyID);
    }

    @Transactional
    @LogActivity(type = "POLICE", detail = "'Poliçe güncellendi. ID: ' + #result.policyId")
    @Override
    public Policy updatePolicy(String policyID, UpdatePolicyRequest request) {
        log.info("Updating policy - policyId: {}", policyID);

        Policy policy = policyRepository.getPolicyByPolicyId(policyID).orElseThrow(() ->{
            log.warn("Policy update failed - policy not found: {}", policyID);
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "This policy does not exist: " + policyID);
        });

        policyMapper.updateEntityFromRequest(request, policy);
        policy.setUpdatedAt(LocalDateTime.now());

        Policy updatedPolicy = policyRepository.save(policy);

        log.info("Policy successfully updated - policyId: {}", policyID);
        eventPublisher.publishEvent(PolicyUpdatedEvent.from(updatedPolicy));
        return updatedPolicy;
    }

    @Transactional
    @LogActivity(type = "POLICE", detail = "'Poliçe yenilendi. ID: ' + #result.policyId")
    @Override
    public Policy renewPolicy(RenewPolicyRequest request) {

        Policy previousPolicy = policyRepository.getPolicyByPolicyId(request.getPreviousPolicyId()).orElseThrow(() ->
        {log.warn("Policy update failed - policy not found: {}", request.getPreviousPolicyId());
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "This policy does not exist: " + request.getPreviousPolicyId());});

        log.info("Creating policy - customerId: {}, type: {}, premium: {}",
                previousPolicy.getCustomerId(), previousPolicy.getType(), request.getPremium());

        if (!customerService.existById(previousPolicy.getCustomerId())) {
            log.warn("Policy creation failed - customer not found: {}", previousPolicy.getCustomerId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Müşteri ID bulunamadı: " + previousPolicy.getCustomerId());
        }

        Policy policy = policyMapper.toEntity(request);

        policy.setPolicyId(idGeneratorService.generatePolicyId(previousPolicy.getType()));

        policy.setCustomerId(previousPolicy.getCustomerId());
        policy.setType(previousPolicy.getType());

        policy.setRenewalSequence(previousPolicy.getRenewalSequence()+1);
        policy.setRootPolicyId(previousPolicy.getRootPolicyId());

        installmentService.createInstallment(policy, request.getInstallment().getValue());
        Policy savedPolicy = policyRepository.save(policy);

        eventPublisher.publishEvent(PolicyCreatedEvent.from(savedPolicy));

        log.info("Policy successfully renewed - policyId: {}, customerId: {}", savedPolicy.getPolicyId(), savedPolicy.getCustomerId());
        return savedPolicy;

    }

}