package com.tunahancoban.policy_tracker.services;

import com.tunahancoban.policy_tracker.model.DTO.CreatePolicyRequest;
import com.tunahancoban.policy_tracker.model.entity.Activity;
import com.tunahancoban.policy_tracker.model.entity.Policy;
import com.tunahancoban.policy_tracker.model.enums.PolicyType;
import com.tunahancoban.policy_tracker.repository.ActivityRepository;
import com.tunahancoban.policy_tracker.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PolicyService {
    private final PolicyRepository policyRepository;
    private final ActivityRepository activityRepository;
    private final CustomerService customerService;

    public List<Policy> getPolicyWithParams(String customerId, String policyId, PolicyType type){
        //We are creating a search criteria here then we are searching this in db
        Policy searchCriteria = new Policy();
        searchCriteria.setCustomerId(customerId);
        searchCriteria.setPolicyId(policyId);
        searchCriteria.setType(type);

        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues().withIgnoreCase().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<Policy> example = Example.of(searchCriteria, matcher);

        return policyRepository.findAll(example);
    }

    public Policy createPolicy(CreatePolicyRequest request){
        if (!customerService.existById(request.getCustomerId())){
            throw new RuntimeException("Customer not found with ID: " + request.getCustomerId());
        }
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new RuntimeException("Policy end date cannot be before start date!");
        }

        Policy policy = new Policy();
        policy.setType(request.getType());
        policy.setPremium(request.getPremium());
        policy.setCustomerId(request.getCustomerId());
        policy.setStartDate(request.getStartDate());
        policy.setEndDate(request.getEndDate());

        policy.setCreatedAt(LocalDateTime.now());
        policy.setUpdatedAt(LocalDateTime.now());

        policy.setPolicyId("test");

        Activity activity = new Activity();
        activity.setCustomerId(policy.getCustomerId());
        activity.setDate(LocalDateTime.now());
        activity.setTitle("Test");
        activity.setDescription("Police kayit edildi police no: " + policy.getPolicyId());

        activityRepository.save(activity);

        return policyRepository.save(policy);
    }

}
