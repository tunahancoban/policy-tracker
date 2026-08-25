package com.tunahancoban.policy_tracker.model.DTO.request.auth;

import com.tunahancoban.policy_tracker.model.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "İsim boş olamaz.")
    private String fullName;

    @NotBlank(message = "Email boş olamaz.")
    @Pattern(
            regexp = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$",
            message = "Geçerli bir email girin."
    )
    private String email;

    @NotBlank(message = "Şifre boş olamaz.")
    private String password;

    @NotNull(message = "Rol seçiniz.")
    private Role role;

}
