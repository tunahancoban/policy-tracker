package com.tunahancoban.policy_tracker.repository;

import com.tunahancoban.policy_tracker.model.entity.Policy;
import com.tunahancoban.policy_tracker.model.enums.PolicyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface PolicyRepository extends MongoRepository<Policy, String> {

    void deleteByPolicyId(String policyId);

    Optional<Policy> getPolicyByPolicyId(String policyId);

    boolean existsByPolicyId(String policyId);

    long countByStartDateLessThanEqualAndEndDateGreaterThanEqual(Instant startDate, Instant endDate);

    long countByEndDateLessThan(Instant date);

    long countByEndDateBetween(Instant start, Instant end);

    Page<Policy> findAll(Pageable pageable);

    @Aggregation(pipeline = {
            "{ '$match': { 'customerId': ?0 } }",
            "{ '$group': { '_id': '$customerId', 'totalPremium': { '$sum': '$premium' } } }"
    })
    List<Map<String, Object>> sumPremiumByCustomerId(String customerId);

    long countByStartDateLessThanEqualAndEndDateGreaterThanEqualAndCustomerId(LocalDate startDate, LocalDate endDate, String customerId);

    long countByEndDateLessThanAndCustomerId(LocalDate date, String customerId);

    long countByEndDateBetweenAndCustomerId(LocalDate start, LocalDate end, String customerId);

    List<Policy> findByEndDateAndNotifiedThresholdsNotContaining(
            LocalDate endDate, Integer threshold
    );

    @Aggregation(pipeline = {
            "{ '$group': { '_id': '$type', 'totalCount': { '$sum': 1 } } }"
    })
    List<Map<String, Object>> countPoliciesGroupedByType();
}