package com.tunahancoban.policy_tracker.model.DTO.request.policy;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Period;

@Getter
@Setter
public class CreateHealthPolicyRequest extends CreatePolicyRequest {
    @NotBlank(message = "TCKN / Pasaport No zorunludur")
    @Size(min = 11, max = 11, message = "TCKN 11 haneli olmalıdır")
    private String identityNumber;

    @NotNull(message = "Doğum tarihi zorunludur")
    @Past(message = "Doğum tarihi geçmiş bir tarih olmalıdır")
    private LocalDate birthDate;

    @NotBlank(message = "Cinsiyet belirtilmelidir")
    private String gender;

    private String healthPlanType; // TSS / OSS
    private String coverageScope;
    private Integer outpatientLimitCount;
    private String networkTier;
    private Boolean maternityCoverage;

    // ── @AssertTrue Business Validasyonları (Entity'den DTO'ya taşındı) ───────

    @AssertTrue(message = "Sigortalının yaşı sağlık poliçesi kabul sınırını (maksimum 69) aşamaz")
    public boolean isBirthDate() {
        if (birthDate != null) {
            int age = Period.between(birthDate, LocalDate.now()).getYears();
            return age >= 0 && age <= 69;
        }
        return true;
    }

    @AssertTrue(message = "Ayakta tedavi teminatı seçildiyse muayene adedi 0'dan büyük olmalıdır")
    public boolean isOutpatientLimitCount() {
        if ("YATARAK_AYAKTA".equalsIgnoreCase(coverageScope)) {
            return outpatientLimitCount != null && outpatientLimitCount > 0;
        }
        return true;
    }

    @AssertTrue(message = "Doğum teminatı yalnızca kadın sigortalılar için seçilebilir")
    public boolean isMaternityCoverage() {
        if (Boolean.TRUE.equals(maternityCoverage)) {
            return "KADIN".equalsIgnoreCase(gender) || "FEMALE".equalsIgnoreCase(gender);
        }
        return true;
    }
}