package com.tunahancoban.policy_tracker.model.DTO.request.policy;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateHousePolicyRequest extends CreatePolicyRequest {
    @NotBlank(message = "UAVT kodu zorunludur")
    @Pattern(regexp = "^[0-9]{10}$", message = "UAVT kodu 10 haneli rakamlardan oluşmalıdır")
    private String uavtCode;

    @NotBlank(message = "Konut kullanım tipi zorunludur (Ev Sahibi / Kiracı)")
    private String residenceType;

    private BigDecimal buildingCoverageLimit;
    private BigDecimal contentsCoverageLimit;
    private Boolean theftCoverage;
    private Boolean waterDamageCoverage;
    private Boolean glassBreakageCoverage;
    private BigDecimal thirdPartyLiabilityLimit;
}