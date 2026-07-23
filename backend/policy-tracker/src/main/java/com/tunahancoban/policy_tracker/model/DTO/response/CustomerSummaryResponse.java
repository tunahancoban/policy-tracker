package com.tunahancoban.policy_tracker.model.DTO.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSummaryResponse {
    private long totalPremium;
    private long activePolicyNumber;
    private long expiringSoonPolicies;
    private long expiredPolicies;
}
