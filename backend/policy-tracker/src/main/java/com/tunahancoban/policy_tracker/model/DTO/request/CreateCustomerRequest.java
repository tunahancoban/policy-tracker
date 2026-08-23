package com.tunahancoban.policy_tracker.model.DTO.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateCustomerRequest {
    @NotBlank(message = "İsim boş olamaz.")
    private String firstName;

    @NotBlank(message = "Soyisim boş olamaz.")
    private String lastName;

    @NotBlank(message = "TC No boş olamaz")
    @Size(min = 11, max = 11, message = "TC No 11 haneli olmalı")
    private String identityNumber;

    @NotBlank(message = "Email boş olamaz")
    @Email(message = "Geçerli bir email giriniz")
    private String email;

    @NotBlank(message = "Telefon numarası boş olamaz")
    private String phoneNumber;

    private String city;
    private String district;
    private String fullAddress;
}
