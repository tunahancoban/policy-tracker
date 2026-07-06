package com.tunahancoban.policy_tracker.model.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryDTO {
    private long totalCustomers;
    private long activePolicyNumber;
    private long expiringSoonPolicies;
    private long expiredPolicies;
}
