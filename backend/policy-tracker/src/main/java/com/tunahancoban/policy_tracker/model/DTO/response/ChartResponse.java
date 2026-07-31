package com.tunahancoban.policy_tracker.model.DTO.response;

import lombok.Builder;
import lombok.Getter;
import java.util.Map;

@Builder
@Getter
public class ChartResponse {
    private Map<String, Long> typeLabels;
    private Map<String, Double> monthlyPremium;
    private long numberOfCriticalPolicies;
    private long numberOfWarningPolicies;
    private long numberOfNormalPolicies;

    //Add new data
}
