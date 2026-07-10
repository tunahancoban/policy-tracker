package com.tunahancoban.policy_tracker.model.DTO.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryResponse {
    private long totalCustomers;
    private long activePolicyNumber;
    private long expiringSoonPolicies;
    private long expiredPolicies;
}
