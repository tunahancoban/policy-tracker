package com.tunahancoban.policy_tracker.controller;

import com.tunahancoban.policy_tracker.model.DTO.request.user.UpdateUserRequest;
import com.tunahancoban.policy_tracker.model.DTO.response.LoginResponse;
import com.tunahancoban.policy_tracker.model.entity.User;
import com.tunahancoban.policy_tracker.service.interfaces.AuthService;
import com.tunahancoban.policy_tracker.service.interfaces.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/api/profile")
@RequiredArgsConstructor
public class RestProfileController {

    private final UserService userService;
    private final AuthService authService;

    @PutMapping
    public ResponseEntity<User> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateUserRequest updateUserRequest) {

        String email = authentication.getName();

        User user = userService.getUserByEmail(email);
        User updatedUser = userService.updateUser(user.getId(), updateUserRequest);

        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping
    public ResponseEntity<User> getProfile(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping(path = "/me")
    public ResponseEntity<LoginResponse> getCurrentUser(){
        LoginResponse loginResponse = authService.getCurrentUser();
        return  ResponseEntity.ok(loginResponse);
    }

}
