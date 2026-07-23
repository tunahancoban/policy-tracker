package com.tunahancoban.policy_tracker.repository;

import com.tunahancoban.policy_tracker.model.entity.Installment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstallmentRepository extends MongoRepository<Installment, String> {
}
