package com.tunahancoban.policy_tracker.model.DTO.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Map;

@Builder
@Getter
public class ChartResponse {
    private Map<String, Long> typeLabels;
    private Map<String, BigDecimal> monthlyPremium;
    private long numberOfCriticalPolicies;
    private long numberOfWarningPolicies;
    private long numberOfNormalPolicies;

    //Add new data
}
