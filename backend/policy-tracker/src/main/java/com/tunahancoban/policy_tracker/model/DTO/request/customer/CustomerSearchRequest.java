package com.tunahancoban.policy_tracker.model.DTO.request.customer;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CustomerSearchRequest {
    private String customerId;
    private String fullName;
    private String identityNumber;
    private String email;
    private String phoneNumber;
    private String city;
    private String district;
    private Boolean isActive;
    private String keyword;
    private LocalDateTime createdDateFrom;
    private LocalDateTime createdDateTo;

    private int page = 0;
    private int size = 20;
    private String sortBy = "customerId";
    private String sortDirection = "ASC";
}