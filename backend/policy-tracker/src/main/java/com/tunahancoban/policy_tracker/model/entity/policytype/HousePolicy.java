package com.tunahancoban.policy_tracker.model.entity.policytype;

import com.tunahancoban.policy_tracker.model.entity.Policy;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class HousePolicy extends Policy {

    @NotBlank(message = "UAVT adres kodu boş bırakılamaz")
    @Pattern(regexp = "^[0-9]{10}$", message = "UAVT adres kodu 10 haneli rakam olmalıdır")
    private String uavtCode;

    @NotNull(message = "Konut kullanım türü (Ev Sahibi/Kiracı) seçilmelidir")
    private String residenceType;

    @DecimalMin(value = "0.0", inclusive = true, message = "Bina teminat bedeli negatif olamaz")
    private BigDecimal buildingCoverageLimit;

    @DecimalMin(value = "0.0", inclusive = true, message = "Eşya teminat bedeli negatif olamaz")
    private BigDecimal contentsCoverageLimit;

    @NotNull(message = "Hırsızlık teminat durumu belirtilmelidir")
    @Builder.Default
    private Boolean theftCoverage = false;

    @NotNull(message = "Dahili su teminat durumu belirtilmelidir")
    @Builder.Default
    private Boolean waterDamageCoverage = false;

    @NotNull(message = "Cam kırılması teminat durumu belirtilmelidir")
    @Builder.Default
    private Boolean glassBreakageCoverage = false;

    @DecimalMin(value = "0.0", inclusive = true, message = "Komşuluk mali sorumluluk limiti negatif olamaz")
    private BigDecimal thirdPartyLiabilityLimit;

    // Bina veya Eşya teminatından en az birinin girilmiş olmasını zorunlu kılan kural
    @AssertTrue(message = "Bina veya eşya teminat bedelinden en az biri sıfırdan büyük olmalıdır")
    private boolean isAtLeastOneCoverageProvided() {
        boolean hasBuilding = buildingCoverageLimit != null && buildingCoverageLimit.compareTo(BigDecimal.ZERO) > 0;
        boolean hasContents = contentsCoverageLimit != null && contentsCoverageLimit.compareTo(BigDecimal.ZERO) > 0;
        return hasBuilding || hasContents;
    }
}