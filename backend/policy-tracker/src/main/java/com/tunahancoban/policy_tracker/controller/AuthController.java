package com.tunahancoban.policy_tracker.controller;

import com.tunahancoban.policy_tracker.model.LoginRequest;
import com.tunahancoban.policy_tracker.model.LoginResponse;
import com.tunahancoban.policy_tracker.model.RestResponse;
import com.tunahancoban.policy_tracker.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/rest/api/auth") //Root address of all paths.
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(path = "/login-request")
    public RestResponse login(@RequestBody LoginRequest request){
        try{
            LoginResponse loginResponse = authService.authenticate(request);
            return RestResponse.success("Login successfully!", loginResponse);
        }catch(Exception e){
            return RestResponse.error(e.getMessage());
        }
    }

}
