package com.tunahancoban.policy_tracker.model.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {

    private String jwt_token;
    private String role;
    private String id;
    private String userEmail;

    public LoginResponse(String role, String id, String userEmail){
        this.role= role;
        this.id=id;
        this.userEmail= userEmail;
    }
}
