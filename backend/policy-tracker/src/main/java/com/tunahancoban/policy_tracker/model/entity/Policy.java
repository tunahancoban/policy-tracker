package com.tunahancoban.policy_tracker.model.entity;

import com.tunahancoban.policy_tracker.model.enums.PolicyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
    private LocalDate startDate; //Policy start date

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate; //Policy end date

    @Positive
    private double premium;

    //Metadata
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}
