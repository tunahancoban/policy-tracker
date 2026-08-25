package com.tunahancoban.policy_tracker.mapper;

import com.tunahancoban.policy_tracker.model.DTO.request.auth.RegisterRequest;
import com.tunahancoban.policy_tracker.model.DTO.request.user.UpdateUserRequest;
import com.tunahancoban.policy_tracker.model.entity.User;
import com.tunahancoban.policy_tracker.model.indexes.UserIndex;
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

    UserIndex toIndex(User user);

    User toEntity(UserIndex document);


    default <T> T mapJsonNullable(JsonNullable<T> jsonNullable) {
        return jsonNullable != null && jsonNullable.isPresent() ? jsonNullable.get() : null;
    }

    @Condition
    default <T> boolean isPresent(JsonNullable<T> jsonNullable) {
        return jsonNullable != null && jsonNullable.isPresent();
    }
}