package com.tunahancoban.policy_tracker.controller;

import com.tunahancoban.policy_tracker.model.DTO.request.auth.LoginRequest;
import com.tunahancoban.policy_tracker.model.DTO.response.LoginResponse;
import com.tunahancoban.policy_tracker.service.interfaces.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/rest/api/auth") //Root address of all paths.
@RequiredArgsConstructor
public class RestAuthController {

    private final AuthService authService;

    @PostMapping(path = "/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request, HttpServletResponse response){


        LoginResponse loginResponse = authService.authenticate(request);

        ResponseCookie cookie = ResponseCookie.from( "jwt_token", loginResponse.getJwt_token())
                        .httpOnly(true).secure(true).path("/").maxAge(Duration.ofDays(7)).sameSite("Strict").build();


        loginResponse.setJwt_token(null);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(loginResponse);
    }

    @PostMapping(path = "/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie deleteCookie = ResponseCookie.from("jwt_token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();
    }

}
