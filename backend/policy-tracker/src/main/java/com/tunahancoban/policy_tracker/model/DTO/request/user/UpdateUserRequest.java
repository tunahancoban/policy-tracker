package com.tunahancoban.policy_tracker.model.DTO.request.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tunahancoban.policy_tracker.model.enums.Role;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateUserRequest {

    private JsonNullable<String> fullName = JsonNullable.undefined();

    private JsonNullable<Boolean> isActive = JsonNullable.undefined();

    private JsonNullable<@Email(message = "This email is not valid. Enter a valid email.") String> email = JsonNullable.undefined();

    private JsonNullable<String> password = JsonNullable.undefined();

    private JsonNullable<Role> role = JsonNullable.undefined();
}