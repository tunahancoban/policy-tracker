package com.tunahancoban.policy_tracker.model.DTO.request.policy;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateCascoPolicyRequest extends CreatePolicyRequest {
    @NotBlank(message = "Plaka alanı zorunludur")
    private String plateNumber;

    @NotBlank(message = "Şasi numarası zorunludur")
    private String chassisNumber;

    @NotBlank(message = "Araç markası zorunludur")
    private String vehicleBrand;

    @NotBlank(message = "Araç modeli zorunludur")
    private String vehicleModel;

    @NotNull(message = "Model yılı zorunludur")
    @Min(1950)
    private Integer modelYear;

    @Positive(message = "Araç kasko bedeli pozitif olmalıdır")
    private BigDecimal vehicleValue;

    private String cascoType;
    private Boolean hasReplacementCar;
    private Integer replacementCarDays;
    private Boolean authorizedServiceOnly;
    private Boolean glassExemption;
}