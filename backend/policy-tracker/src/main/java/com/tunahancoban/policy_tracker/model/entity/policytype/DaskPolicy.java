package com.tunahancoban.policy_tracker.model.entity.policytype;

import com.tunahancoban.policy_tracker.model.entity.Policy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class DaskPolicy extends Policy {
    private String uavtCode; // 10 haneli adres kodu
    private Integer grossSquareMeters;
    private String buildingConstructionType; // Betonarme, Yığma Kagir vb.
    private Integer buildingConstructionYear;
    private Integer totalFloorCount;
    private Integer apartmentFloor;
    private Integer earthquakeZone; // 1-5 arası deprem bölgesi
}