package com.tunahancoban.policy_tracker.model.entity.policytype;

import com.tunahancoban.policy_tracker.model.entity.Policy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class TrafficPolicy extends Policy {
    private String plateNumber;
    private String chassisNumber;
    private String engineNumber;
    private String vehicleUsageType;
    private Integer noClaimDiscountStep;
    private Boolean hasImm;
    private BigDecimal immLimit;
}