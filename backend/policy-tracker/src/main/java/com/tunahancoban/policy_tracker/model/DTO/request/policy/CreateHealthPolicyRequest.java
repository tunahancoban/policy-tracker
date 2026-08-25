package com.tunahancoban.policy_tracker.model.DTO.request.policy;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

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
}