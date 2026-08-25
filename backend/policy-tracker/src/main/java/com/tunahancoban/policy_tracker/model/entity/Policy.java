package com.tunahancoban.policy_tracker.model.entity;

import com.tunahancoban.policy_tracker.model.enums.InstallmentOptions;
import com.tunahancoban.policy_tracker.model.enums.PolicyStatus;
import com.tunahancoban.policy_tracker.model.enums.PolicyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Document(collection = "policies")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Policy {
    @Id
    private String id;

    @Indexed(unique = true)
    private String policyId;

    @NotBlank
    @Indexed
    private String customerId;

    private InstallmentOptions installment;

    @Builder.Default
    private PolicyStatus isActive = PolicyStatus.ACTIVE;

    @NotNull
    private PolicyType type;

    @NotNull
    private LocalDate startDate;

    @Indexed
    @NotNull
    private LocalDate endDate; //Policy end date

    @Positive
    private BigDecimal premium;

    private String responsibleUserId;

    private String note;

    //Renewal Metadata
    private String previousPolicyId;

    private String rootPolicyId;


    @Builder.Default
    private Integer renewalSequence = 0;

    //Metadata
    @Builder.Default
    @CreatedDate
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @LastModifiedDate
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime deletedAt=null;

    private Set<Integer> notifiedThresholds = new HashSet<>(); //30-15-7 day

}
