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
public class UpdateCascoPolicyRequest extends UpdatePolicyRequest {

    private JsonNullable<String> plateNumber = JsonNullable.undefined();

    private JsonNullable<String> chassisNumber = JsonNullable.undefined();

    private JsonNullable<String> vehicleBrand = JsonNullable.undefined();

    private JsonNullable<String> vehicleModel = JsonNullable.undefined();

    private JsonNullable<Integer> modelYear = JsonNullable.undefined();

    private JsonNullable<BigDecimal> vehicleValue = JsonNullable.undefined();

    private JsonNullable<String> cascoType = JsonNullable.undefined();

    private JsonNullable<Boolean> hasReplacementCar = JsonNullable.undefined();

    private JsonNullable<Integer> replacementCarDays = JsonNullable.undefined();

    private JsonNullable<Boolean> authorizedServiceOnly = JsonNullable.undefined();

    private JsonNullable<Boolean> glassExemption = JsonNullable.undefined();
}
