package com.tunahancoban.policy_tracker.model.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "customers")
@Builder(toBuilder = true)
public class Customer {

    @Id
    private String id;

    @Indexed(unique = true)
    private String customerId;

    @NotBlank(message = "İsim boş olamaz")
    private String fullName;

    @NotBlank(message = "T.C. No boş olamaz")
    @Size(min =11, max=11, message = "T.C. No 11 haneli olmalı")
    private String identityNumber;

    @NotBlank(message = "Email boş olamaz")
    @Email(message = "Geçerli bir email giriniz")
    private String email;

    @NotBlank(message = "Telefon numarası boş olamaz")
    private String phoneNumber;


    private String city;
    private String district;
    private String fullAddress;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
    @Builder.Default
    private LocalDateTime deletedAt = LocalDateTime.now();
    @Builder.Default
    private Boolean isActive = true;

}
