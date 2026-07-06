package com.tunahancoban.policy_tracker.model.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateCustomerRequest {
    @NotBlank(message = "Name cannot be null")
    private String firstName;

    @NotBlank(message = "Lastname cannot be null")
    private String lastName;

    @NotBlank(message = "T.C. No cannot be null")
    @Size(min = 11, max = 11, message = "T.C. No must be 11 digit")
    private String identityNumber;

    @NotBlank(message = "Email cannot be null")
    @Email(message = "Enter a valid email")
    private String email;

    @NotBlank(message = "Phone number cannot be null")
    private String phoneNumber;

    private String city;
    private String district;
    private String fullAddress;
}
