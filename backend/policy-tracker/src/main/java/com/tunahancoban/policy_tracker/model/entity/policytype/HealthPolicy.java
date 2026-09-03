package com.tunahancoban.policy_tracker.model.entity.policytype;

import com.tunahancoban.policy_tracker.model.entity.Policy;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.Period;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class HealthPolicy extends Policy {

    @NotBlank(message = "Kimlik/Pasaport numarası boş bırakılamaz")
    @Pattern(
            regexp = "^([1-9][0-9]{10}|[A-Z0-9]{7,20})$",
            message = "Geçerli bir TCKN (11 haneli) veya Pasaport numarası giriniz"
    )
    private String identityNumber;

    @NotNull(message = "Doğum tarihi boş bırakılamaz")
    @Past(message = "Doğum tarihi geçmiş bir tarih olmalıdır")
    private LocalDate birthDate;

    @NotBlank(message = "Cinsiyet seçimi zorunludur")
    @Pattern(regexp = "^(ERKEK|KADIN|MALE|FEMALE)$", message = "Cinsiyet 'ERKEK' veya 'KADIN' olmalıdır")
    private String gender;

    @NotBlank(message = "Sağlık planı türü seçilmelidir")
    @Pattern(regexp = "^(TSS|OSS)$", message = "Plan türü 'TSS' veya 'OSS' olmalıdır")
    private String healthPlanType;

    @NotBlank(message = "Teminat kapsamı seçilmelidir")
    @Pattern(
            regexp = "^(YATARAK|YATARAK_AYAKTA)$",
            message = "Kapsam 'YATARAK' veya 'YATARAK_AYAKTA' olmalıdır"
    )
    private String coverageScope;

    @Min(value = 0, message = "Ayakta tedavi limiti 0'dan küçük olamaz")
    @Max(value = 30, message = "Ayakta tedavi limiti en fazla 30 olabilir")
    private Integer outpatientLimitCount;

    @NotBlank(message = "Anlaşmalı hastane ağı (Network) boş bırakılamaz")
    @Size(min = 2, max = 50, message = "Network adı 2 ile 50 karakter arasında olmalıdır")
    private String networkTier;

    @NotNull(message = "Doğum teminatı durumu belirtilmelidir")
    @Builder.Default
    private Boolean maternityCoverage = false;

    @AssertTrue(message = "Ayakta tedavi teminatı seçildiyse muayene adedi 0'dan büyük olmalıdır")
    private boolean isOutpatientLimitCount() {
        if ("YATARAK_AYAKTA".equalsIgnoreCase(coverageScope)) {
            return outpatientLimitCount != null && outpatientLimitCount > 0;
        }
        return true;
    }

    @AssertTrue(message = "Doğum teminatı yalnızca kadın sigortalılar için seçilebilir")
    private boolean isMaternityCoverage() {
        if (Boolean.TRUE.equals(maternityCoverage)) {
            return "KADIN".equalsIgnoreCase(gender) || "FEMALE".equalsIgnoreCase(gender);
        }
        return true;
    }

    @AssertTrue(message = "Sigortalının yaşı sağlık poliçesi kabul sınırını (maksimum 69) aşamaz")
    private boolean isBirthDate() {
        if (birthDate != null) {
            int age = Period.between(birthDate, LocalDate.now()).getYears();
            return age >= 0 && age <= 69;
        }
        return true;
    }
}