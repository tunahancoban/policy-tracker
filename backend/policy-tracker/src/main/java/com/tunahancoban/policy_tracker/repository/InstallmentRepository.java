package com.tunahancoban.policy_tracker.repository;

import com.tunahancoban.policy_tracker.model.entity.Installment;
import com.tunahancoban.policy_tracker.model.enums.PaymentStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InstallmentRepository extends MongoRepository<Installment, String>, InstallmentRepositoryCustom {

    List<Installment> findAllByPolicyId(String policyId);
    List<Installment> findByDueDateAndStatusAndNotifiedThresholdsNotContaining(LocalDate dueDate, PaymentStatus status, Integer threshold);
}
