package com.tunahancoban.policy_tracker.model.entity.policytype;

import com.tunahancoban.policy_tracker.model.entity.Policy;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Year;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class DaskPolicy extends Policy {

    @NotBlank(message = "UAVT adres kodu boş bırakılamaz")
    @Pattern(regexp = "^[0-9]{10}$", message = "UAVT adres kodu 10 haneli rakam olmalıdır")
    private String uavtCode;

    @NotNull(message = "Brüt metrekare alanı zorunludur")
    @Min(value = 10, message = "Brüt metrekare en az 10 m² olmalıdır")
    @Max(value = 1000, message = "Brüt metrekare en fazla 1000 m² olabilir")
    private Integer grossSquareMeters;

    @NotBlank(message = "Bina yapı tarzı seçilmelidir")
    @Pattern(
            regexp = "^(BETONARME|CELIK|YIGMA_KAGIR|DIGER)$",
            message = "Bina yapı tarzı 'BETONARME', 'CELIK', 'YIGMA_KAGIR' veya 'DIGER' olmalıdır"
    )
    private String buildingConstructionType;

    @NotNull(message = "Bina inşa yılı boş bırakılamaz")
    @Min(value = 1900, message = "Bina inşa yılı 1900 yılından küçük olamaz")
    private Integer buildingConstructionYear;

    @NotNull(message = "Toplam kat sayısı zorunludur")
    @Min(value = 1, message = "Toplam kat sayısı en az 1 olmalıdır")
    @Max(value = 100, message = "Toplam kat sayısı 100'den büyük olamaz")
    private Integer totalFloorCount;

    @NotNull(message = "Bulunduğu kat bilgisi zorunludur")
    @Min(value = -5, message = "Bulunduğu kat -5'ten (bodrum katlar) küçük olamaz")
    @Max(value = 100, message = "Bulunduğu kat 100'den büyük olamaz")
    private Integer apartmentFloor;

    @NotNull(message = "Deprem risk bölgesi boş bırakılamaz")
    @Min(value = 1, message = "Deprem bölgesi en az 1 olabilir")
    @Max(value = 5, message = "Deprem bölgesi en fazla 5 olabilir")
    private Integer earthquakeZone;

    @AssertTrue(message = "Bulunduğu kat, binanın toplam kat sayısından büyük olamaz")
    private boolean isApartmentFloorValid() {
        if (apartmentFloor != null && totalFloorCount != null) {
            return apartmentFloor <= totalFloorCount;
        }
        return true;
    }

    @AssertTrue(message = "Bina inşa yılı içinde bulunulan yıldan büyük olamaz")
    private boolean isConstructionYearValid() {
        if (buildingConstructionYear != null) {
            return buildingConstructionYear <= Year.now().getValue();
        }
        return true;
    }
}