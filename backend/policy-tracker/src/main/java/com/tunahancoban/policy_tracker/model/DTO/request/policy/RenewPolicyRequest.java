package com.tunahancoban.policy_tracker.model.DTO.request.policy;

import com.tunahancoban.policy_tracker.model.enums.InstallmentOptions;
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
    private String responsibleUserId;

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
