package com.tunahancoban.policy_tracker.model.entity;

import com.tunahancoban.policy_tracker.model.enums.PolicyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "policies")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Policy {
    @Id
    private String id;

    private String policyId;

    @NotBlank
    private String customerId;

    @NotNull
    private PolicyType type;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate; //Policy end date

    @Positive
    private Double premium;

    //Metadata
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    public String toString() {
        return "Policy.PolicyBuilder(id=" + this.id + ", policyId=" + this.policyId + ", customerId=" + this.customerId + ", type=" + this.type + ", startDate=" + this.startDate + ", endDate=" + this.endDate + ", premium=" + this.premium + ", createdAt=" + this.createdAt + ", updatedAt=" + this.updatedAt + ")";
    }
}
