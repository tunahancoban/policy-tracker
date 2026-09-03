package com.tunahancoban.policy_tracker.model.DTO.request.policy;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.tunahancoban.policy_tracker.model.enums.InstallmentOptions;
import com.tunahancoban.policy_tracker.model.enums.InsuranceCompany;
import com.tunahancoban.policy_tracker.model.enums.PolicyType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CreateTrafficPolicyRequest.class, name = "TRAFIK"),
        @JsonSubTypes.Type(value = CreateCascoPolicyRequest.class, name = "KASKO"),
        @JsonSubTypes.Type(value = CreateDaskPolicyRequest.class, name = "DASK"),
        @JsonSubTypes.Type(value = CreateHousePolicyRequest.class, name = "KONUT"),
        @JsonSubTypes.Type(value = CreateHealthPolicyRequest.class, name = "SAGLIK")
})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreatePolicyRequest {
    
    @NotBlank
    private String customerId;

    @NotNull
    private PolicyType type;

    @NotNull
    private InsuranceCompany company;
    private String note;

    @NotNull
    private LocalDate startDate; //Policy start date

    @NotNull
    private LocalDate endDate; //Policy end date

    @NotBlank
    private String responsibleUserId;

    @Positive
    private BigDecimal premium;

    @NotNull
    private InstallmentOptions installment;

    @AssertTrue(message = "Poliçe bitiş günü başlangıç gününden önce olamaz.")
    public boolean isEndDate() {
        if (startDate == null || endDate == null) {
            return true;
        }
        return !endDate.isBefore(startDate);
    }

}
