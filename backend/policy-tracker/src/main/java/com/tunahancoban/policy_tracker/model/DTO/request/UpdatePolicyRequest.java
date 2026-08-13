package com.tunahancoban.policy_tracker.model.DTO.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tunahancoban.policy_tracker.model.enums.PolicyType;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.openapitools.jackson.nullable.JsonNullable;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePolicyRequest {

    private JsonNullable<String> customerId = JsonNullable.undefined();

    private JsonNullable<PolicyType> type = JsonNullable.undefined();

    @JsonFormat(pattern = "yyyy/MM/dd")
    private JsonNullable<LocalDate> startDate = JsonNullable.undefined();
    @JsonFormat(pattern = "yyyy/MM/dd")
    private JsonNullable<LocalDate> endDate = JsonNullable.undefined();

    private JsonNullable<BigDecimal> premium = JsonNullable.undefined();

    private JsonNullable<String> responsibleUserId =  JsonNullable.undefined();

    private JsonNullable<String> note = JsonNullable.undefined();
}