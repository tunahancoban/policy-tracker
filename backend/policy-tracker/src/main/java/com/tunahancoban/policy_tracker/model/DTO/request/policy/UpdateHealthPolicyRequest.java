package com.tunahancoban.policy_tracker.model.DTO.request.policy;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.openapitools.jackson.nullable.JsonNullable;

import java.time.LocalDate;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateHealthPolicyRequest extends UpdatePolicyRequest {

    private JsonNullable<String> identityNumber = JsonNullable.undefined();

    @JsonFormat(pattern = "yyyy-MM-dd")
    private JsonNullable<LocalDate> birthDate = JsonNullable.undefined();

    private JsonNullable<String> gender = JsonNullable.undefined();

    private JsonNullable<String> healthPlanType = JsonNullable.undefined();

    private JsonNullable<String> coverageScope = JsonNullable.undefined();

    private JsonNullable<Integer> outpatientLimitCount = JsonNullable.undefined();

    private JsonNullable<String> networkTier = JsonNullable.undefined();

    private JsonNullable<Boolean> maternityCoverage = JsonNullable.undefined();
}
