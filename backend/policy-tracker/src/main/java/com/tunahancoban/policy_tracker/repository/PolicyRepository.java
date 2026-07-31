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

@Repository
public interface PolicyRepository extends MongoRepository<Policy, String> {
    boolean existsByPolicyId(String policyId);
    void deleteByPolicyId(String policyId);
    Policy findByPolicyId(String policyId);
    long countByStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate startDate, LocalDate endDate);
    long countByEndDateLessThan(LocalDate date);
    long countByEndDateBetween(Instant start, Instant end);
    Page<Policy> findByCustomerId(String customerId, Pageable pageable);
    Page<Policy> findByType(PolicyType type, Pageable pageable);
    Page<Policy> findAll(Pageable pageable);

    @Aggregation(pipeline = {
            "{ '$match': { 'customerId': ?0 } }",
            "{ '$group': { '_id': '$customerId', 'totalPremium': { '$sum': '$premium' } } }"
    })
    List<Map<String, Object>> sumPremiumByCustomerId(String customerId);
    long countByStartDateLessThanEqualAndEndDateGreaterThanEqualAndCustomerId(LocalDate startDate, LocalDate endDate, String customerId);
    long countByEndDateLessThanAndCustomerId(LocalDate date, String customerId);
    long countByEndDateBetweenAndCustomerId(LocalDate start, LocalDate end, String customerId);
    @Aggregation(pipeline = {
            "{ '$group': { '_id': '$type', 'totalCount': { '$sum': 1 } } }"
    })
    List<Map<String, Object>> countPoliciesGroupedByType();
    @Aggregation(pipeline = {
            "{ '$match': { 'startDate': { '$gte': ?0 } } }",
            "{ '$group': { '_id': { '$dateToString': { 'format': '%Y-%m', 'date': '$startDate' } }, 'totalPremium': { '$sum': '$premium' } } }",
            "{ '$sort': { '_id': 1 } }"
    })
    List<Map<String, Object>> findMonthlyPremiumEarnings(LocalDate sixMonthsAgo);}
