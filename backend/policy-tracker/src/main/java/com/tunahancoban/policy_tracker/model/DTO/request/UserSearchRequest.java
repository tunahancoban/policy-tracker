package com.tunahancoban.policy_tracker.model.DTO.request;

import com.tunahancoban.policy_tracker.model.enums.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSearchRequest {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private Boolean isActive;
    private String keyword;


    private int page = 0;
    private int size = 20;
    private String sortBy = "firstName";
    private String sortDirection = "ASC";
}