package com.tunahancoban.policy_tracker.model.DTO.request.policy;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateDaskPolicyRequest extends CreatePolicyRequest {
    @NotBlank(message = "UAVT kodu zorunludur")
    @Pattern(regexp = "^[0-9]{10}$", message = "UAVT kodu 10 haneli rakamlardan oluşmalıdır")
    private String uavtCode;

    @NotNull(message = "Brüt metrekare zorunludur")
    @Positive(message = "Metrekare 0'dan büyük olmalıdır")
    private Integer grossSquareMeters;

    private String buildingConstructionType;
    private Integer buildingConstructionYear;
    private Integer totalFloorCount;
    private Integer apartmentFloor;
    private Integer earthquakeZone;
}