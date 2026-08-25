package com.tunahancoban.policy_tracker.model.entity.policytype;

import com.tunahancoban.policy_tracker.model.entity.Policy;
import lombok.AllArgsConstructor;
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
    private String uavtCode;
    private String residenceType; // Ev Sahibi, Kiracı
    private BigDecimal buildingCoverageLimit; // Bina teminat bedeli
    private BigDecimal contentsCoverageLimit; // Eşya teminat bedeli
    private Boolean theftCoverage; // Hırsızlık
    private Boolean waterDamageCoverage; // Dahili su/su baskını
    private Boolean glassBreakageCoverage; // Cam kırılması
    private BigDecimal thirdPartyLiabilityLimit; // Komşuluk sorumluluk limiti
}