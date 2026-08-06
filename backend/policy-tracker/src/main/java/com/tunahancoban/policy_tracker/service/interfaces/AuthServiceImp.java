package com.tunahancoban.policy_tracker.service.interfaces;

import com.tunahancoban.policy_tracker.model.DTO.request.LoginRequest;
import com.tunahancoban.policy_tracker.model.DTO.response.LoginResponse;

public interface AuthServiceImp {
    LoginResponse authenticate(LoginRequest request);
    LoginResponse getCurrentUser();

}
