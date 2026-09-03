package com.tunahancoban.policy_tracker.repository;

import com.tunahancoban.policy_tracker.model.entity.Policy;
import com.tunahancoban.policy_tracker.model.enums.PolicyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface PolicyRepository extends MongoRepository<Policy, String> {

    Optional<Policy> getPolicyByPolicyId(String policyId);

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

    @Query(value = "{ '_class': 'com.tunahancoban.policy_tracker.model.entity.policytype.TrafficPolicy', "
            + "'chassisNumber': ?0, 'startDate': { '$lte': ?1 }, 'endDate': { '$gte': ?2 } }",
            exists = true)
    boolean existsOverlappingTrafficPolicy(String chassisNumber, LocalDate newEndDate, LocalDate newStartDate);

    @Query(value = "{ '_class': 'com.tunahancoban.policy_tracker.model.entity.policytype.TrafficPolicy', "
            + "'chassisNumber': ?0, 'startDate': { '$lte': ?1 }, 'endDate': { '$gte': ?2 }, 'policyId': { '$ne': ?3 } }",
            exists = true)
    boolean existsOverlappingTrafficPolicyExcluding(String chassisNumber, LocalDate newEndDate, LocalDate newStartDate, String excludePolicyId);

    @Query(value = "{ '_class': 'com.tunahancoban.policy_tracker.model.entity.policytype.DaskPolicy', "
            + "'uavtCode': ?0, 'startDate': { '$lte': ?1 }, 'endDate': { '$gte': ?2 } }",
            exists = true)
    boolean existsOverlappingDaskPolicy(String uavtCode, LocalDate newEndDate, LocalDate newStartDate);

    @Query(value = "{ '_class': 'com.tunahancoban.policy_tracker.model.entity.policytype.DaskPolicy', "
            + "'uavtCode': ?0, 'startDate': { '$lte': ?1 }, 'endDate': { '$gte': ?2 }, 'policyId': { '$ne': ?3 } }",
            exists = true)
    boolean existsOverlappingDaskPolicyExcluding(String uavtCode, LocalDate newEndDate, LocalDate newStartDate, String excludePolicyId);

    @Query(value = "{ '_class': 'com.tunahancoban.policy_tracker.model.entity.policytype.HealthPolicy', "
            + "'identityNumber': ?0, 'startDate': { '$lte': ?1 }, 'endDate': { '$gte': ?2 } }",
            exists = true)
    boolean existsOverlappingHealthPolicy(String identityNumber, LocalDate newEndDate, LocalDate newStartDate);

    @Query(value = "{ '_class': 'com.tunahancoban.policy_tracker.model.entity.policytype.HealthPolicy', "
            + "'identityNumber': ?0, 'startDate': { '$lte': ?1 }, 'endDate': { '$gte': ?2 }, 'policyId': { '$ne': ?3 } }",
            exists = true)
    boolean existsOverlappingHealthPolicyExcluding(String identityNumber, LocalDate newEndDate, LocalDate newStartDate, String excludePolicyId);

    @Query(value = "{ '_class': 'com.tunahancoban.policy_tracker.model.entity.policytype.HousePolicy', "
            + "'uavtCode': ?0, 'residenceType': ?1, 'startDate': { '$lte': ?2 }, 'endDate': { '$gte': ?3 } }",
            exists = true)
    boolean existsOverlappingHousePolicyByResidenceType(
            String uavtCode, String residenceType, LocalDate newEndDate, LocalDate newStartDate);

    @Query(value = "{ '_class': 'com.tunahancoban.policy_tracker.model.entity.policytype.HousePolicy', "
            + "'uavtCode': ?0, 'residenceType': ?1, 'startDate': { '$lte': ?2 }, 'endDate': { '$gte': ?3 }, 'policyId': { '$ne': ?4 } }",
            exists = true)
    boolean existsOverlappingHousePolicyByResidenceTypeExcluding(
            String uavtCode, String residenceType, LocalDate newEndDate, LocalDate newStartDate, String excludePolicyId);

    @Query(value = "{ '_class': 'com.tunahancoban.policy_tracker.model.entity.policytype.CascoPolicy', "
            + "'chassisNumber': ?0, 'startDate': { '$lte': ?1 }, 'endDate': { '$gte': ?2 } }",
            exists = true)
    boolean existsOverlappingCascoPolicy(String chassisNumber, LocalDate newEndDate, LocalDate newStartDate);

    @Query(value = "{ '_class': 'com.tunahancoban.policy_tracker.model.entity.policytype.CascoPolicy', "
            + "'chassisNumber': ?0, 'startDate': { '$lte': ?1 }, 'endDate': { '$gte': ?2 }, 'policyId': { '$ne': ?3 } }",
            exists = true)
    boolean existsOverlappingCascoPolicyExcluding(String chassisNumber, LocalDate newEndDate, LocalDate newStartDate, String excludePolicyId);

}