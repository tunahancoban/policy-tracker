package com.tunahancoban.policy_tracker.model.DTO.request.policy;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.tunahancoban.policy_tracker.model.enums.InsuranceCompany;
import com.tunahancoban.policy_tracker.model.enums.PolicyType;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.openapitools.jackson.nullable.JsonNullable;

import java.math.BigDecimal;
import java.time.LocalDate;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true,
        defaultImpl = UpdatePolicyRequest.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = UpdateTrafficPolicyRequest.class, name = "TRAFIK"),
        @JsonSubTypes.Type(value = UpdateCascoPolicyRequest.class, name = "KASKO"),
        @JsonSubTypes.Type(value = UpdateDaskPolicyRequest.class, name = "DASK"),
        @JsonSubTypes.Type(value = UpdateHousePolicyRequest.class, name = "KONUT"),
        @JsonSubTypes.Type(value = UpdateHealthPolicyRequest.class, name = "SAGLIK")
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePolicyRequest {

    private JsonNullable<String> customerId = JsonNullable.undefined();

    private JsonNullable<PolicyType> type = JsonNullable.undefined();

    private JsonNullable<InsuranceCompany> company;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private JsonNullable<LocalDate> startDate = JsonNullable.undefined();
    @JsonFormat(pattern = "yyyy-MM-dd")
    private JsonNullable<LocalDate> endDate = JsonNullable.undefined();

    private JsonNullable<BigDecimal> premium = JsonNullable.undefined();

    private JsonNullable<Boolean> isActive = JsonNullable.undefined();

    private JsonNullable<String> responsibleUserId =  JsonNullable.undefined();

    private JsonNullable<String> note = JsonNullable.undefined();
}