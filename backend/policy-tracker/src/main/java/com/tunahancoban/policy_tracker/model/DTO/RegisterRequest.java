package com.tunahancoban.policy_tracker.model.DTO;

import com.tunahancoban.policy_tracker.model.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "Name cannot be null")
    private String firstName;

    @NotBlank(message = "Lastname cannot be null")
    private String lastName;

    @NotBlank(message = "Email cannot be null")
    @Pattern(
            regexp = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$",
            message = "Please enter a valid email"
    )
    private String email;

    @NotBlank(message = "Password cannot be null")
    private String password;

    @NotNull(message = "Role cannot be null")
    private Role role;

}
