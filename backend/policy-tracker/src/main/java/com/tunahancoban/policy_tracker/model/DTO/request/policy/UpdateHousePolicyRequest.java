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
public class UpdateHousePolicyRequest extends UpdatePolicyRequest {

    private JsonNullable<String> uavtCode = JsonNullable.undefined();

    private JsonNullable<String> residenceType = JsonNullable.undefined();

    private JsonNullable<BigDecimal> buildingCoverageLimit = JsonNullable.undefined();

    private JsonNullable<BigDecimal> contentsCoverageLimit = JsonNullable.undefined();

    private JsonNullable<Boolean> theftCoverage = JsonNullable.undefined();

    private JsonNullable<Boolean> waterDamageCoverage = JsonNullable.undefined();

    private JsonNullable<Boolean> glassBreakageCoverage = JsonNullable.undefined();

    private JsonNullable<BigDecimal> thirdPartyLiabilityLimit = JsonNullable.undefined();
}
