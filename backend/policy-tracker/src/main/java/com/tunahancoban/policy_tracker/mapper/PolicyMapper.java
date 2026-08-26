package com.tunahancoban.policy_tracker.mapper;

import com.tunahancoban.policy_tracker.model.DTO.request.policy.*;

import com.tunahancoban.policy_tracker.model.entity.*;
import com.tunahancoban.policy_tracker.model.entity.policytype.*;
import com.tunahancoban.policy_tracker.model.indexes.PolicyIndex;
import org.mapstruct.*;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION,
        builder = @Builder(disableBuilder = true)
)
public interface PolicyMapper {

    // ==========================================
    // 1. CREATE MAPPINGS (Polymorphic DTO -> Entity)
    // ==========================================
    @SubclassMapping(source = CreateTrafficPolicyRequest.class, target = TrafficPolicy.class)
    @SubclassMapping(source = CreateCascoPolicyRequest.class, target = CascoPolicy.class)
    @SubclassMapping(source = CreateDaskPolicyRequest.class, target = DaskPolicy.class)
    @SubclassMapping(source = CreateHousePolicyRequest.class, target = HousePolicy.class)
    @SubclassMapping(source = CreateHealthPolicyRequest.class, target = HealthPolicy.class)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "policyId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Policy toEntity(CreatePolicyRequest request);

    TrafficPolicy toEntity(CreateTrafficPolicyRequest request);
    CascoPolicy toEntity(CreateCascoPolicyRequest request);
    DaskPolicy toEntity(CreateDaskPolicyRequest request);
    HousePolicy toEntity(CreateHousePolicyRequest request);
    HealthPolicy toEntity(CreateHealthPolicyRequest request);


    // ==========================================
    // 2. RENEWAL MAPPING
    // ==========================================
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "policyId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "rootPolicyId", ignore = true)
    @Mapping(target = "renewalSequence", ignore = true)
    void updateEntityFromRenewRequest(RenewPolicyRequest request, @MappingTarget Policy policy);

    // ==========================================
    // 3. ELASTICSEARCH DOCUMENT MAPPINGS
    // ==========================================
    @SubclassMapping(source = TrafficPolicy.class, target = PolicyIndex.class)
    @SubclassMapping(source = CascoPolicy.class, target = PolicyIndex.class)
    @SubclassMapping(source = DaskPolicy.class, target = PolicyIndex.class)
    @SubclassMapping(source = HousePolicy.class, target = PolicyIndex.class)
    @SubclassMapping(source = HealthPolicy.class, target = PolicyIndex.class)
    PolicyIndex toDocument(Policy policy);

    PolicyIndex toDocument(TrafficPolicy policy);
    PolicyIndex toDocument(CascoPolicy policy);
    PolicyIndex toDocument(DaskPolicy policy);
    PolicyIndex toDocument(HousePolicy policy);
    PolicyIndex toDocument(HealthPolicy policy);

    default Policy toEntity(PolicyIndex document) {
        if (document == null) {
            return null;
        }
        if (document.getType() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Poliçe tipi bulunamadı.");
        }

        return switch (document.getType()) {
            case TRAFIK -> toTrafficPolicyEntity(document);
            case KASKO -> toCascoPolicyEntity(document);
            case DASK -> toDaskPolicyEntity(document);
            case KONUT -> toHousePolicyEntity(document);
            case SAGLIK -> toHealthPolicyEntity(document);
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bilinmeyen poliçe tipi: " + document.getType());
        };
    }

    TrafficPolicy toTrafficPolicyEntity(PolicyIndex document);
    CascoPolicy toCascoPolicyEntity(PolicyIndex document);
    DaskPolicy toDaskPolicyEntity(PolicyIndex document);
    HousePolicy toHousePolicyEntity(PolicyIndex document);
    HealthPolicy toHealthPolicyEntity(PolicyIndex document);


    // ==========================================
    // 4. UPDATE MAPPINGS (Polymorphic In-Place Update)
    // ==========================================
    default void updateEntityFromRequest(UpdatePolicyRequest request, @MappingTarget Policy policy) {
        if (request instanceof UpdateTrafficPolicyRequest r && policy instanceof TrafficPolicy p) {
            updateTrafficFromRequest(r, p);
        } else if (request instanceof UpdateCascoPolicyRequest r && policy instanceof CascoPolicy p) {
            updateCascoFromRequest(r, p);
        } else if (request instanceof UpdateDaskPolicyRequest r && policy instanceof DaskPolicy p) {
            updateDaskFromRequest(r, p);
        } else if (request instanceof UpdateHousePolicyRequest r && policy instanceof HousePolicy p) {
            updateHouseFromRequest(r, p);
        } else if (request instanceof UpdateHealthPolicyRequest r && policy instanceof HealthPolicy p) {
            updateHealthFromRequest(r, p);
        } else {
            updateCommonFieldsFromRequest(request, policy);
        }
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "policyId", ignore = true)
    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateCommonFieldsFromRequest(UpdatePolicyRequest request, @MappingTarget Policy policy);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "policyId", ignore = true)
    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateTrafficFromRequest(UpdateTrafficPolicyRequest request, @MappingTarget TrafficPolicy policy);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "policyId", ignore = true)
    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateCascoFromRequest(UpdateCascoPolicyRequest request, @MappingTarget CascoPolicy policy);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "policyId", ignore = true)
    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateDaskFromRequest(UpdateDaskPolicyRequest request, @MappingTarget DaskPolicy policy);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "policyId", ignore = true)
    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateHouseFromRequest(UpdateHousePolicyRequest request, @MappingTarget HousePolicy policy);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "policyId", ignore = true)
    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateHealthFromRequest(UpdateHealthPolicyRequest request, @MappingTarget HealthPolicy policy);

    // ==========================================
    // 5. HELPER AND VALIDATION
    // ==========================================
    @BeforeMapping
    default void validateDateRangeOnUpdate(UpdatePolicyRequest request, @MappingTarget Policy policy) {
        LocalDate effectiveStartDate = (request.getStartDate() != null && request.getStartDate().isPresent() && request.getStartDate().get() != null)
                ? request.getStartDate().get()
                : policy.getStartDate();

        LocalDate effectiveEndDate = (request.getEndDate() != null && request.getEndDate().isPresent() && request.getEndDate().get() != null)
                ? request.getEndDate().get()
                : policy.getEndDate();

        if (effectiveStartDate != null && effectiveEndDate != null && effectiveEndDate.isBefore(effectiveStartDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Poliçe bitiş günü başlangıç gününden önce olamaz.");
        }
    }

    default <T> T mapJsonNullable(JsonNullable<T> jsonNullable) {
        return jsonNullable != null && jsonNullable.isPresent() ? jsonNullable.get() : null;
    }

    @Condition
    default <T> boolean isPresent(JsonNullable<T> jsonNullable) {
        return jsonNullable != null && jsonNullable.isPresent();
    }

    // ==========================================
// 6. RENEWAL CLONE (Polymorphic Policy -> Policy copy)
// ==========================================
    @SubclassMapping(source = TrafficPolicy.class, target = TrafficPolicy.class)
    @SubclassMapping(source = CascoPolicy.class, target = CascoPolicy.class)
    @SubclassMapping(source = DaskPolicy.class, target = DaskPolicy.class)
    @SubclassMapping(source = HousePolicy.class, target = HousePolicy.class)
    @SubclassMapping(source = HealthPolicy.class, target = HealthPolicy.class)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "policyId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Policy clonePolicy(Policy source);

    TrafficPolicy clonePolicy(TrafficPolicy source);
    CascoPolicy clonePolicy(CascoPolicy source);
    DaskPolicy clonePolicy(DaskPolicy source);
    HousePolicy clonePolicy(HousePolicy source);
    HealthPolicy clonePolicy(HealthPolicy source);
}