package com.tunahancoban.policy_tracker.model.entity;

import com.tunahancoban.policy_tracker.model.enums.PaymentStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "installments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Installment {
    @Id
    private String id;

    private String policyId;
    private Integer installmentNo;
    private Double amount;
    private PaymentStatus status;
    private LocalDate paidAt;
    private LocalDate dueDate;
    //Metadata
    private LocalDateTime createdAt=LocalDateTime.now();
    private LocalDateTime updatedAt=LocalDateTime.now();

}
