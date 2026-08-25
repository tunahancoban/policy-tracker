package com.tunahancoban.policy_tracker.service.interfaces;

import com.tunahancoban.policy_tracker.model.DTO.request.policy.CreatePolicyRequest;
import com.tunahancoban.policy_tracker.model.DTO.request.policy.RenewPolicyRequest;
import com.tunahancoban.policy_tracker.model.DTO.request.policy.UpdatePolicyRequest;
import com.tunahancoban.policy_tracker.model.entity.Policy;
import com.tunahancoban.policy_tracker.model.enums.PolicyStatus;
import com.tunahancoban.policy_tracker.model.enums.PolicyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PolicyService {

    Page<Policy> getPolicyWithParams(String customerId, String policyId, PolicyType type, String responsibleUserId, PolicyStatus active , Pageable pageable);
    Policy getPolicyById(String policyId);
    Policy createPolicy(CreatePolicyRequest request);
    void deletePolicy(String policyID);
    Policy updatePolicy(String policyID, UpdatePolicyRequest request);
    Policy renewPolicy(RenewPolicyRequest request);

}
