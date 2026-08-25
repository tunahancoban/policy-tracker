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
public class CascoPolicy extends Policy {
    private String plateNumber;
    private String chassisNumber;
    private String vehicleBrand;
    private String vehicleModel;
    private Integer modelYear;
    private BigDecimal vehicleValue;
    private String cascoType;
    private Boolean hasReplacementCar;
    private Integer replacementCarDays;
    private Boolean authorizedServiceOnly;
    private Boolean glassExemption;
}