package com.tunahancoban.policy_tracker.model.entity.policytype;

import com.tunahancoban.policy_tracker.model.entity.Policy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class HealthPolicy extends Policy {
    private String identityNumber; // TCKN veya Pasaport No
    private LocalDate birthDate;
    private String gender; // Erkek / Kadın
    private String healthPlanType; // TSS (Tamamlayıcı) veya ÖSS (Özel)
    private String coverageScope; // Sadece Yatarak / Yatarak + Ayakta
    private Integer outpatientLimitCount; // Yıllık ayakta muayene adedi (örn: 8, 10)
    private String networkTier; // Network A, Network B vb.
    private Boolean maternityCoverage; // Doğum teminatı
}