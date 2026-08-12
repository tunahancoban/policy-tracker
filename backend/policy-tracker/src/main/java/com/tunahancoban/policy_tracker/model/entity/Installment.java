package com.tunahancoban.policy_tracker.model.entity;

import com.tunahancoban.policy_tracker.model.enums.PaymentStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Document(collection = "installments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@CompoundIndexes({
        @CompoundIndex(name = "duedate_status_idx", def = "{'dueDate': 1, 'status': 1}")
})
public class Installment {
    @Id
    private String id;

    @Indexed
    private String policyId;
    @Indexed
    private String customerId;
    private Integer installmentNo;
    private BigDecimal amount;
    @Builder.Default
    private PaymentStatus status = PaymentStatus.UNPAID;
    private LocalDate paidAt;
    private LocalDate dueDate;
    //Metadata
    private LocalDateTime createdAt=LocalDateTime.now();
    private LocalDateTime updatedAt=LocalDateTime.now();
    @Builder.Default
    private Set<Integer> notifiedThresholds = new HashSet<>();

}
