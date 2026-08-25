package com.tunahancoban.policy_tracker.model.DTO.request.policy;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.openapitools.jackson.nullable.JsonNullable;

import java.math.BigDecimal;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTrafficPolicyRequest extends UpdatePolicyRequest {

    private JsonNullable<String> plateNumber = JsonNullable.undefined();

    private JsonNullable<String> chassisNumber = JsonNullable.undefined();

    private JsonNullable<String> engineNumber = JsonNullable.undefined();

    private JsonNullable<String> vehicleUsageType = JsonNullable.undefined();

    private JsonNullable<Integer> noClaimDiscountStep = JsonNullable.undefined();

    private JsonNullable<Boolean> hasImm = JsonNullable.undefined();

    private JsonNullable<BigDecimal> immLimit = JsonNullable.undefined();
}
