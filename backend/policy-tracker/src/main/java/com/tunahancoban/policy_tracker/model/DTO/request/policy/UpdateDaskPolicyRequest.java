package com.tunahancoban.policy_tracker.model.DTO.request.policy;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDaskPolicyRequest extends UpdatePolicyRequest {

    private JsonNullable<String> uavtCode = JsonNullable.undefined();

    private JsonNullable<Integer> grossSquareMeters = JsonNullable.undefined();

    private JsonNullable<String> buildingConstructionType = JsonNullable.undefined();

    private JsonNullable<Integer> buildingConstructionYear = JsonNullable.undefined();

    private JsonNullable<Integer> totalFloorCount = JsonNullable.undefined();

    private JsonNullable<Integer> apartmentFloor = JsonNullable.undefined();

    private JsonNullable<Integer> earthquakeZone = JsonNullable.undefined();
}
