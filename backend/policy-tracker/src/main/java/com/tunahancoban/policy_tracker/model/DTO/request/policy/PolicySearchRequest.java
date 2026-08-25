package com.tunahancoban.policy_tracker.model.DTO.request.policy;

import com.tunahancoban.policy_tracker.model.enums.PolicyStatus;
import com.tunahancoban.policy_tracker.model.enums.PolicyType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class PolicySearchRequest {
    private String customerId;
    private String policyId;
    private String responsibleUserId;
    private PolicyStatus isActive;
    private PolicyType type;
    private String keyword;
    private LocalDate endDateFrom;
    private LocalDate endDateTo;
    private BigDecimal premiumMin;
    private BigDecimal premiumMax;

    private int page = 0;
    private int size = 20;
    private String sortBy = "endDate";
    private String sortDirection = "ASC"; // ASC / DESC
}