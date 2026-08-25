package com.tunahancoban.policy_tracker.model.DTO.request.user;

import com.tunahancoban.policy_tracker.model.enums.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSearchRequest {

    private String id;
    private String fullName;
    private String email;
    private Role role;
    private Boolean isActive;
    private String keyword;


    private int page = 0;
    private int size = 20;
    private String sortBy = "fullName";
    private String sortDirection = "ASC";
}