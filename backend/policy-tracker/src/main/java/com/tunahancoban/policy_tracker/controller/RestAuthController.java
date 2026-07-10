package com.tunahancoban.policy_tracker.controller;

import com.tunahancoban.policy_tracker.model.DTO.request.LoginRequest;
import com.tunahancoban.policy_tracker.model.DTO.response.LoginResponse;
import com.tunahancoban.policy_tracker.model.DTO.response.RestResponse;
import com.tunahancoban.policy_tracker.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:9000", allowCredentials = "true")
@RestController
@RequestMapping("/rest/api/auth") //Root address of all paths.
@RequiredArgsConstructor
public class RestAuthController {

    private final AuthService authService;

    @PostMapping(path = "/login-request")
    public ResponseEntity<RestResponse<LoginResponse>> login(@RequestBody LoginRequest request, HttpServletResponse response){

            LoginResponse loginResponse = authService.authenticate(request);
            Cookie cookie = new Cookie("jwt_token", loginResponse.getToken());
            cookie.setHttpOnly(true);
            cookie.setSecure(false);
            cookie.setPath("/");
            cookie.setMaxAge(24*60*60*7); //Token lifecycle 7 days

            response.addCookie(cookie);

            LoginResponse newLoginResponse = new LoginResponse(loginResponse.getRole(), loginResponse.getUserEmail());
            return ResponseEntity.ok(RestResponse.success("Başarıyla giriş yapıldı", newLoginResponse));
    }
    @GetMapping(path = "/me")
    public ResponseEntity<RestResponse<LoginResponse>> getCurrentUser(){
        LoginResponse loginResponse = authService.getCurrentUser();
        return  ResponseEntity.ok(RestResponse.success("Bilgiler başarıyla yüklendi", loginResponse));
    }

}
