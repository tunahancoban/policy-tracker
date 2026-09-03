package com.tunahancoban.policy_tracker.service;

import com.tunahancoban.policy_tracker.annotation.LogActivity;
import com.tunahancoban.policy_tracker.mapper.PolicyMapper;
import com.tunahancoban.policy_tracker.model.DTO.events.PolicyEvent;
import com.tunahancoban.policy_tracker.model.DTO.request.policy.*;
import com.tunahancoban.policy_tracker.model.entity.Policy;
import com.tunahancoban.policy_tracker.model.enums.EventTypes;
import com.tunahancoban.policy_tracker.model.enums.PolicyType;
import com.tunahancoban.policy_tracker.model.exceptions.BusinessValidationException;
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
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
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
    public Page<Policy> getPolicyWithParams(String customerId, String policyId, PolicyType type, String responsibleUserId, Boolean active, Pageable pageable) {
        log.debug("Searching policies - customerId: {}, policyId: {}, type: {}, page: {}",
                customerId, policyId, type, pageable);

        //We are creating a search criteria here then we are searching this in db
        Policy searchCriteria = Policy.builder()
                .policyId(policyId)
                .customerId(customerId)
                .isActive(active)
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

        validateNoOverlap(request);

        Policy policy = policyMapper.toEntity(request);

        policy.setPolicyId(idGeneratorService.generatePolicyId(request.getType()));
        policy.setPreviousPolicyId(null); //Policy is created now so it does not have any prev policy
        policy.setRootPolicyId(policy.getPolicyId()); // Root PolicyID itself

        installmentService.createInstallment(policy, request.getInstallment().getValue());
        Policy savedPolicy = policyRepository.save(policy);

        eventPublisher.publishEvent(PolicyEvent.from(savedPolicy, EventTypes.CREATE));

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

        policy.setIsActive(false);
        policy.setDeletedAt(LocalDateTime.now());
        installmentService.deleteInstallment(policyID);

        policyRepository.save(policy);
        eventPublisher.publishEvent(PolicyEvent.from(policy, EventTypes.DELETE));

        log.info("Policy successfully deleted - policyId: {}", policyID);
    }

    @Transactional
    @LogActivity(type = "POLICE", detail = "'Poliçe güncellendi. ID: ' + #result.policyId")
    @Override
    public Policy updatePolicy(String policyID, UpdatePolicyRequest request) {
        log.info("Updating policy - policyId: {}", policyID);

        Policy policy = policyRepository.getPolicyByPolicyId(policyID).orElseThrow(() -> {
            log.warn("Policy update failed - policy not found: {}", policyID);
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "This policy does not exist: " + policyID);
        });
        if (request.getType() != null && request.getType().isPresent()) {
            PolicyType requestedType = request.getType().get();
            if (requestedType != null && !policy.getType().equals(requestedType)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.format("Poliçe türü değiştirilemez! Mevcut: %s, Gelen: %s", policy.getType(), requestedType));
            }
        }

        validateNoOverlapForUpdate(policyID, policy, request);

        policyMapper.updateEntityFromRequest(request, policy);
        policy.setUpdatedAt(LocalDateTime.now());

        Policy updatedPolicy = policyRepository.save(policy);
        eventPublisher.publishEvent(PolicyEvent.from(updatedPolicy, EventTypes.UPDATE));

        log.info("Policy successfully updated - policyId: {}", policyID);
        return updatedPolicy;
    }

    @Transactional
    @LogActivity(type = "POLICE", detail = "'Poliçe yenilendi. ID: ' + #result.policyId")
    @Override
    public Policy renewPolicy(RenewPolicyRequest request) {
        Policy previousPolicy = policyRepository.getPolicyByPolicyId(request.getPreviousPolicyId())
                .orElseThrow(() -> {
                    log.warn("Policy update failed - policy not found: {}", request.getPreviousPolicyId());
                    return new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "This policy does not exist: " + request.getPreviousPolicyId());
                });

        if (!customerService.existById(previousPolicy.getCustomerId())) {
            log.warn("Policy creation failed - customer not found: {}", previousPolicy.getCustomerId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Müşteri ID bulunamadı: " + previousPolicy.getCustomerId());
        }

        Policy policy = policyMapper.clonePolicy(previousPolicy);

        policyMapper.updateEntityFromRenewRequest(request, policy);
        policy.setId(null);
        policy.setPolicyId(idGeneratorService.generatePolicyId(previousPolicy.getType()));
        policy.setCustomerId(previousPolicy.getCustomerId());
        policy.setType(previousPolicy.getType());
        policy.setRenewalSequence(previousPolicy.getRenewalSequence() + 1);
        policy.setRootPolicyId(previousPolicy.getRootPolicyId());

        installmentService.createInstallment(policy, request.getInstallment().getValue());
        Policy savedPolicy = policyRepository.save(policy);
        eventPublisher.publishEvent(PolicyEvent.from(savedPolicy, EventTypes.CREATE));

        log.info("Policy successfully renewed - policyId: {}, customerId: {}",
                savedPolicy.getPolicyId(), savedPolicy.getCustomerId());
        return savedPolicy;
    }

    private void validateNoOverlap(CreatePolicyRequest request) {
        boolean overlaps;
        String field;
        String message;

        switch (request.getType()) {
            case TRAFIK -> {
                var r = (CreateTrafficPolicyRequest) request;
                overlaps = policyRepository.existsOverlappingTrafficPolicy(r.getChassisNumber(), r.getEndDate(), r.getStartDate());
                field = "chassisNumber";
                message = "Bu şase Numarası ile bu tarihler arasında kayıtlı bir poliçe zaten var";
            }
            case DASK -> {
                var r = (CreateDaskPolicyRequest) request;
                overlaps = policyRepository.existsOverlappingDaskPolicy(r.getUavtCode(), r.getEndDate(), r.getStartDate());
                field = "uavtCode";
                message = "Bu UAVT Adres Kodu ile bu tarihler arasında kayıtlı bir DASK poliçesi zaten var";
            }
            case SAGLIK -> {
                var r = (CreateHealthPolicyRequest) request;
                overlaps = policyRepository.existsOverlappingHealthPolicy(r.getIdentityNumber(), r.getEndDate(), r.getStartDate());
                field = "identityNumber";
                message = "Bu kimlik/pasaport numarası ile bu tarihler arasında kayıtlı bir sağlık poliçesi zaten var";
            }
            case KONUT -> {
                var r = (CreateHousePolicyRequest) request;
                overlaps = policyRepository.existsOverlappingHousePolicyByResidenceType(r.getUavtCode(), r.getResidenceType(), r.getEndDate(), r.getStartDate());
                field = "uavtCode";
                message = String.format("Bu UAVT Adres Kodu ve '%s' kullanım türü ile bu tarihler arasında kayıtlı bir konut poliçesi zaten var", r.getResidenceType());
            }
            case KASKO -> {
                var r = (CreateCascoPolicyRequest) request;
                overlaps = policyRepository.existsOverlappingCascoPolicy(r.getChassisNumber(), r.getEndDate(), r.getStartDate());
                field = "chassisNumber";
                message = "Bu şasi numarası ile bu tarihler arasında kayıtlı bir kasko poliçesi zaten var";
            }
            default -> { return; }
        }

        if (overlaps) {
            throw new BusinessValidationException(field, message, HttpStatus.CONFLICT);
        }
    }


    private void validateNoOverlapForUpdate(String policyId, Policy existing, UpdatePolicyRequest request) {
        boolean overlaps;
        String field;
        String message;

        LocalDate effectiveStartDate = (request.getStartDate() != null && request.getStartDate().isPresent())
                ? request.getStartDate().get() : existing.getStartDate();
        LocalDate effectiveEndDate = (request.getEndDate() != null && request.getEndDate().isPresent())
                ? request.getEndDate().get() : existing.getEndDate();

        switch (existing.getType()) {
            case TRAFIK -> {
                if (!(request instanceof UpdateTrafficPolicyRequest r)) return;
                String chassis = (r.getChassisNumber() != null && r.getChassisNumber().isPresent())
                        ? r.getChassisNumber().get() : null;
                if (chassis == null) {
                    if (effectiveStartDate.equals(existing.getStartDate()) && effectiveEndDate.equals(existing.getEndDate())) return;
                    chassis = ((com.tunahancoban.policy_tracker.model.entity.policytype.TrafficPolicy) existing).getChassisNumber();
                }
                overlaps = policyRepository.existsOverlappingTrafficPolicyExcluding(chassis, effectiveEndDate, effectiveStartDate, policyId);
                field = "chassisNumber";
                message = "Bu şase numarası ile bu tarihler arasında kayıtlı başka bir trafik poliçesi zaten var";
            }
            case DASK -> {
                if (!(request instanceof UpdateDaskPolicyRequest r)) return;
                String uavt = (r.getUavtCode() != null && r.getUavtCode().isPresent())
                        ? r.getUavtCode().get() : null;
                if (uavt == null) {
                    if (effectiveStartDate.equals(existing.getStartDate()) && effectiveEndDate.equals(existing.getEndDate())) return;
                    uavt = ((com.tunahancoban.policy_tracker.model.entity.policytype.DaskPolicy) existing).getUavtCode();
                }
                overlaps = policyRepository.existsOverlappingDaskPolicyExcluding(uavt, effectiveEndDate, effectiveStartDate, policyId);
                field = "uavtCode";
                message = "Bu UAVT Adres Kodu ile bu tarihler arasında kayıtlı başka bir DASK poliçesi zaten var";
            }
            case SAGLIK -> {
                if (!(request instanceof UpdateHealthPolicyRequest r)) return;
                String identity = (r.getIdentityNumber() != null && r.getIdentityNumber().isPresent())
                        ? r.getIdentityNumber().get() : null;
                if (identity == null) {
                    if (effectiveStartDate.equals(existing.getStartDate()) && effectiveEndDate.equals(existing.getEndDate())) return;
                    identity = ((com.tunahancoban.policy_tracker.model.entity.policytype.HealthPolicy) existing).getIdentityNumber();
                }
                overlaps = policyRepository.existsOverlappingHealthPolicyExcluding(identity, effectiveEndDate, effectiveStartDate, policyId);
                field = "identityNumber";
                message = "Bu kimlik/pasaport numarası ile bu tarihler arasında kayıtlı başka bir sağlık poliçesi zaten var";
            }
            case KONUT -> {
                if (!(request instanceof UpdateHousePolicyRequest r)) return;
                var existingHouse = (com.tunahancoban.policy_tracker.model.entity.policytype.HousePolicy) existing;
                String uavt = (r.getUavtCode() != null && r.getUavtCode().isPresent())
                        ? r.getUavtCode().get() : existingHouse.getUavtCode();
                String residenceType = (r.getResidenceType() != null && r.getResidenceType().isPresent())
                        ? r.getResidenceType().get() : existingHouse.getResidenceType();
                if (uavt == null || residenceType == null) return;
                overlaps = policyRepository.existsOverlappingHousePolicyByResidenceTypeExcluding(uavt, residenceType, effectiveEndDate, effectiveStartDate, policyId);
                field = "uavtCode";
                message = String.format("Bu UAVT Adres Kodu ve '%s' kullanım türü ile bu tarihler arasında kayıtlı başka bir konut poliçesi zaten var", residenceType);
            }
            case KASKO -> {
                if (!(request instanceof UpdateCascoPolicyRequest r)) return;
                String chassis = (r.getChassisNumber() != null && r.getChassisNumber().isPresent())
                        ? r.getChassisNumber().get() : null;
                if (chassis == null) {
                    if (effectiveStartDate.equals(existing.getStartDate()) && effectiveEndDate.equals(existing.getEndDate())) return;
                    chassis = ((com.tunahancoban.policy_tracker.model.entity.policytype.CascoPolicy) existing).getChassisNumber();
                }
                overlaps = policyRepository.existsOverlappingCascoPolicyExcluding(chassis, effectiveEndDate, effectiveStartDate, policyId);
                field = "chassisNumber";
                message = "Bu şasi numarası ile bu tarihler arasında kayıtlı başka bir kasko poliçesi zaten var";
            }
            default -> { return; }
        }

        if (overlaps) {
            throw new BusinessValidationException(field, message, HttpStatus.CONFLICT);
        }
    }

}