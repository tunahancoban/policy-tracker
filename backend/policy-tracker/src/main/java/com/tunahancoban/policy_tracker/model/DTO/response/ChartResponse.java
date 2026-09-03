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

    // Aylık yenilenme sayıları: { "TRAFIK": { "1": 5, "2": 3 }, "SAGLIK": { "1": 2 } }
    private Map<String, Map<Integer, Long>> monthlyRenewals;
}
