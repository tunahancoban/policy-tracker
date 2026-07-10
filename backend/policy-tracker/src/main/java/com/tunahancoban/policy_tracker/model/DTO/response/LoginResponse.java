package com.tunahancoban.policy_tracker.model.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private String role;
    private String userEmail;

    public LoginResponse(String role, String userEmail){
        this.role= role;
        this.userEmail= userEmail;
    }
}
