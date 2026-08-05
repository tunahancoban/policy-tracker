package com.tunahancoban.policy_tracker.mapper;

import com.tunahancoban.policy_tracker.model.DTO.request.RegisterRequest;
import com.tunahancoban.policy_tracker.model.DTO.request.UpdateUserRequest;
import com.tunahancoban.policy_tracker.model.entity.User;
import org.mapstruct.*;
import org.openapitools.jackson.nullable.JsonNullable;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    User toEntity(RegisterRequest request);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateEntityFromRequest(UpdateUserRequest request, @MappingTarget User user);


    default <T> T mapJsonNullable(JsonNullable<T> jsonNullable) {
        return jsonNullable != null && jsonNullable.isPresent() ? jsonNullable.get() : null;
    }

    @Condition
    default <T> boolean isPresent(JsonNullable<T> jsonNullable) {
        return jsonNullable != null && jsonNullable.isPresent();
    }
}