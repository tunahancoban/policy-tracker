package com.tunahancoban.policy_tracker.mapper;

import com.tunahancoban.policy_tracker.model.DTO.request.CreatePolicyRequest;
import com.tunahancoban.policy_tracker.model.DTO.request.RenewPolicyRequest;
import com.tunahancoban.policy_tracker.model.DTO.request.UpdatePolicyRequest;
import com.tunahancoban.policy_tracker.model.entity.Policy;
import org.mapstruct.*;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface PolicyMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "policyId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Policy toEntity(CreatePolicyRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "policyId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "rootPolicyId", ignore = true)
    @Mapping(target = "renewalSequence", ignore = true)
    Policy toEntity(RenewPolicyRequest request);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "policyId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "installment", ignore = true)
    void updateEntityFromRequest(UpdatePolicyRequest request, @MappingTarget Policy policy);

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
}