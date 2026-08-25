package com.tunahancoban.policy_tracker.mapper;


import com.tunahancoban.policy_tracker.model.DTO.request.customer.CreateCustomerRequest;
import com.tunahancoban.policy_tracker.model.DTO.request.customer.UpdateCustomerRequest;
import com.tunahancoban.policy_tracker.model.entity.Customer;
import com.tunahancoban.policy_tracker.model.indexes.CustomerIndex;
import org.mapstruct.*;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.Optional;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)

public interface CustomerMapper {


    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    Customer toEntity(CreateCustomerRequest request);


    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateEntityFromRequest(UpdateCustomerRequest request, @MappingTarget Customer customer);

    CustomerIndex toIndex(Customer customer);

    Customer toEntity(CustomerIndex document);

    default <T> T mapJsonNullable(JsonNullable<T> jsonNullable) {
        return jsonNullable != null && jsonNullable.isPresent() ? jsonNullable.get() : null;
    }

    @Condition
    default <T> boolean isPresent(Optional<T> optional) {
        return optional != null && optional.isPresent();
    }

    default <T> T unwrapOptional(Optional<T> optional) {
        return optional != null ? optional.orElse(null) : null;
    }
}