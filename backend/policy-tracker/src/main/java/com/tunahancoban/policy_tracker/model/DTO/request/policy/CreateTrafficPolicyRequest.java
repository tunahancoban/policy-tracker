package com.tunahancoban.policy_tracker.model.DTO.request.policy;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateTrafficPolicyRequest extends CreatePolicyRequest {
    @NotBlank(message = "Plaka alanı zorunludur")
    private String plateNumber;

    @NotBlank(message = "Şasi numarası zorunludur")
    private String chassisNumber;

    private String engineNumber;
    private String vehicleUsageType;

    @Min(value = 0, message = "Hasarsızlık kademesi 0'dan küçük olamaz")
    @Max(value = 8, message = "Hasarsızlık kademesi 8'den büyük olamaz")
    private Integer noClaimDiscountStep;

    private Boolean hasImm;
    private BigDecimal immLimit;
}