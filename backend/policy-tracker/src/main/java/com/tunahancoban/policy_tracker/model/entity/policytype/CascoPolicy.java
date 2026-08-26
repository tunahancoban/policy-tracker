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
import java.time.Year;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class CascoPolicy extends Policy {

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

    @NotBlank(message = "Araç markası boş bırakılamaz")
    @Size(min = 2, max = 50, message = "Araç markası 2-50 karakter arasında olmalıdır")
    private String vehicleBrand;

    @NotBlank(message = "Araç modeli boş bırakılamaz")
    @Size(min = 1, max = 50, message = "Araç modeli 1-50 karakter arasında olmalıdır")
    private String vehicleModel;

    @NotNull(message = "Model yılı boş bırakılamaz")
    @Min(value = 1990, message = "Kasko için model yılı en az 1990 olabilir")
    private Integer modelYear;

    @NotNull(message = "Araç kasko değeri boş bırakılamaz")
    @DecimalMin(value = "0.01", inclusive = true, message = "Araç kasko değeri 0'dan büyük olmalıdır")
    private BigDecimal vehicleValue;

    @NotBlank(message = "Kasko türü seçilmelidir")
    @Pattern(
            regexp = "^(DAR_KASKO|KASKO|GENISLETILMIS_KASKO|TAM_KASKO)$",
            message = "Kasko türü 'DAR_KASKO', 'KASKO', 'GENISLETILMIS_KASKO' veya 'TAM_KASKO' olmalıdır"
    )
    private String cascoType;

    @NotNull(message = "İkame araç teminat durumu belirtilmelidir")
    @Builder.Default
    private Boolean hasReplacementCar = false;

    @Min(value = 0, message = "İkame araç gün sayısı 0'dan küçük olamaz")
    @Max(value = 60, message = "İkame araç gün sayısı en fazla 60 olabilir")
    private Integer replacementCarDays;

    @NotNull(message = "Yetkili servis tercihi belirtilmelidir")
    @Builder.Default
    private Boolean authorizedServiceOnly = false;

    @NotNull(message = "Cam muafiyeti durumu belirtilmelidir")
    @Builder.Default
    private Boolean glassExemption = false;

    // Kural 1: Model yılı içinde bulunulan yıldan en fazla 1 yıl ileri olabilir (yeni tescil araçlar için)
    @AssertTrue(message = "Model yılı geçerli bir yıl olmalıdır")
    private boolean isModelYearValid() {
        if (modelYear != null) {
            int currentYear = Year.now().getValue();
            return modelYear <= (currentYear + 1);
        }
        return true;
    }

    // Kural 2: İkame araç teminatı seçildiyse gün sayısı 0'dan büyük olmalıdır
    @AssertTrue(message = "İkame araç teminatı seçildiyse gün sayısı 0'dan büyük olmalıdır")
    private boolean isReplacementCarDaysValid() {
        if (Boolean.TRUE.equals(hasReplacementCar)) {
            return replacementCarDays != null && replacementCarDays > 0;
        }
        return true;
    }
}