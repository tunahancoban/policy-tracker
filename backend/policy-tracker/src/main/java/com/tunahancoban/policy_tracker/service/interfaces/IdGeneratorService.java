package com.tunahancoban.policy_tracker.service.interfaces;

import com.tunahancoban.policy_tracker.model.enums.PolicyType;

public interface IdGeneratorService {
    long getNextSequence(String seqName);
    String generateCustomerId();
    String generatePolicyId(PolicyType type);
}
