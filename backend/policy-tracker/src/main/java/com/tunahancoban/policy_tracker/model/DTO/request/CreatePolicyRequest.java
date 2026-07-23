package com.tunahancoban.policy_tracker.model.DTO.request;

import com.tunahancoban.policy_tracker.model.enums.InstallmentOptions;
import com.tunahancoban.policy_tracker.model.enums.PolicyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreatePolicyRequest {
    
    @NotBlank
    private String customerId;

    @NotNull
    private PolicyType type;

    private String note;

    @NotNull
    private LocalDate startDate; //Policy start date

    @NotNull
    private LocalDate endDate; //Policy end date

    @Positive
    private Double premium;

    private InstallmentOptions installmentNumber;

}
