package com.tunahancoban.policy_tracker.model.DTO.request;

import com.tunahancoban.policy_tracker.model.enums.InstallmentOptions;
import com.tunahancoban.policy_tracker.model.enums.PolicyType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class RenewPolicyRequest {

    private String note;

    @NotNull
    private LocalDate startDate; //Policy start date

    @NotNull
    private LocalDate endDate; //Policy end date

    @Positive
    private BigDecimal premium;

    @NotNull
    private InstallmentOptions installment;

    @NotBlank
    private String previousPolicyId;

    @AssertTrue(message = "Poliçe bitiş günü başlangıç gününden önce olamaz.")
    public boolean isValidDateRange() {
        if (startDate == null || endDate == null) {
            return true;
        }
        return !endDate.isBefore(startDate);
    }
}
