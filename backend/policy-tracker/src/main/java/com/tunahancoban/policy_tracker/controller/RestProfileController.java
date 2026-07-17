package com.tunahancoban.policy_tracker.controller;

import com.tunahancoban.policy_tracker.model.DTO.response.LoginResponse;
import com.tunahancoban.policy_tracker.model.DTO.response.RestResponse;
import com.tunahancoban.policy_tracker.model.entity.User;
import com.tunahancoban.policy_tracker.service.AuthService;
import com.tunahancoban.policy_tracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rest/api/profile")
@RequiredArgsConstructor
public class RestProfileController {

    private final UserService userService;
    private final AuthService authService;

    @PutMapping(path = "/update-profile")
    public ResponseEntity<RestResponse<Void>> updateProfile(Authentication authentication, @RequestBody Map<String, Object> updates){
        String email = authentication.getName();

        List<User> userList = userService.getUserWithParam(null, null, null, email, null);
        if(userList==null||userList.isEmpty()){
            return ResponseEntity.ok(RestResponse.error("Kullanıcı bulunamadı.."));
        }
        String userId = userList.getFirst().getId();

        userService.updateUser(userId, updates);
        return ResponseEntity.ok(RestResponse.success("Kullanıcı bilgileri başarıyla güncellendi."));
    }

    @GetMapping(path = "/get-profile")
    public ResponseEntity<RestResponse<User>> getProfile(Authentication authentication){
        String email  = authentication.getName();
        List<User> userList = userService.getUserWithParam(null, null, null, email, null);
        if(userList==null||userList.isEmpty()){
            return ResponseEntity.ok(RestResponse.error("Kullanıcı bulunamadı.."));
        }

        return ResponseEntity.ok(RestResponse.success("Kullanıcı bilgileri başarıyla getirildi.", userList.getFirst()));
    }

    @GetMapping(path = "/me")
    public ResponseEntity<RestResponse<LoginResponse>> getCurrentUser(){
        LoginResponse loginResponse = authService.getCurrentUser();
        return  ResponseEntity.ok(RestResponse.success("Bilgiler başarıyla yüklendi", loginResponse));
    }

}
