package com.tunahancoban.policy_tracker.model.entity.policytype;

import com.tunahancoban.policy_tracker.model.entity.Policy;
import jakarta.validation.constraints.*;
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
public class TrafficPolicy extends Policy {

    @NotBlank(message = "Plaka alanı boş bırakılamaz")
    @Pattern(
            regexp = "^(0[1-9]|[1-7][0-9]|8[0-1])[A-Z]{1,3}[0-9]{2,4}$",
            message = "Geçersiz Türkiye plaka formatı"
    )
    private String plateNumber;

    @NotBlank(message = "Şasi numarası boş bırakılamaz")
    @Pattern(
            regexp = "^[A-HJ-NPR-Z0-9]{17}$",
            message = "Şasi numarası 17 karakter olmalı ve I, O, Q harflerini içermemelidir"
    )
    private String chassisNumber;

    @NotBlank(message = "Motor numarası boş bırakılamaz")
    @Size(min = 6, max = 20, message = "Motor numarası 6-20 karakter arasında olmalıdır")
    private String engineNumber;

    @NotNull(message = "Araç kullanım tarzı seçilmelidir")
    private String vehicleUsageType;

    @NotNull(message = "Hasarsızlık kademesi boş bırakılamaz")
    @Min(value = 0, message = "Hasarsızlık basamağı en az 0 olabilir")
    @Max(value = 8, message = "Hasarsızlık basamağı en fazla 8 olabilir")
    private Integer noClaimDiscountStep;

    @NotNull(message = "İMM durumu belirtilmelidir")
    private Boolean hasImm;

    @DecimalMin(value = "0.0", inclusive = true, message = "İMM limiti 0 veya daha büyük olmalıdır")
    private BigDecimal immLimit;

    // Sınıf içi koşullu validasyon
    @AssertTrue(message = "İMM teminatı seçildiyse geçerli bir İMM limiti girilmelidir")
    private boolean isImmLimitValid() {
        if (Boolean.TRUE.equals(hasImm)) {
            return immLimit != null && immLimit.compareTo(BigDecimal.ZERO) > 0;
        }
        return true;
    }
}