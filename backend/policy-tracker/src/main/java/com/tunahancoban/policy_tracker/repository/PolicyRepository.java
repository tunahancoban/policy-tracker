package com.tunahancoban.policy_tracker.repository;

import com.tunahancoban.policy_tracker.model.entity.Policy;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface PolicyRepository extends MongoRepository<Policy, String> {
    long countByStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate startDate, LocalDate endDate);
    long countByEndDateLessThan(LocalDate date);
    long countByEndDateBetween(LocalDate start, LocalDate end);
}
