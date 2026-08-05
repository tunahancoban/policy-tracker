package com.tunahancoban.policy_tracker.mapper;

import com.tunahancoban.policy_tracker.model.DTO.request.CreatePolicyRequest;
import com.tunahancoban.policy_tracker.model.DTO.request.UpdatePolicyRequest;
import com.tunahancoban.policy_tracker.model.entity.Policy;
import org.mapstruct.*;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.Optional;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
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
    @Mapping(target = "note", ignore = true)        
    @Mapping(target = "installment", ignore = true)
    void updateEntityFromRequest(UpdatePolicyRequest request, @MappingTarget Policy policy);


    default <T> T mapJsonNullable(JsonNullable<T> jsonNullable) {
        return jsonNullable != null && jsonNullable.isPresent() ? jsonNullable.get() : null;
    }

    @Condition
    default <T> boolean isPresent(JsonNullable<T> jsonNullable) {
        return jsonNullable != null && jsonNullable.isPresent();
    }
    default <T> T unwrapOptional(Optional<T> optional) {
        return optional != null ? optional.orElse(null) : null;
    }

}