package com.tunahancoban.policy_tracker.controller;

import com.tunahancoban.policy_tracker.model.DTO.request.RegisterRequest;
import com.tunahancoban.policy_tracker.model.DTO.response.RestResponse;
import com.tunahancoban.policy_tracker.model.enums.Role;
import com.tunahancoban.policy_tracker.model.entity.User;
import com.tunahancoban.policy_tracker.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/rest/api/user")
@RequiredArgsConstructor
public class RestUserController {

    private final UserService userService;

    // 1. FIND User
    @GetMapping(path= "/with-params")
    public ResponseEntity<RestResponse<List<User>>> getUserWithParam(@RequestParam(name = "id", required = false) String id,
                                                                     @RequestParam(name = "firstName", required = false) String firstName,
                                                                     @RequestParam(name = "lastName", required = false) String lastName,
                                                                     @RequestParam(name = "email", required = false) String email,
                                                                     @RequestParam(name = "role", required = false) Role role) {

        List<User> users = userService.getUserWithParam(id, firstName, lastName, email, role);
        return ResponseEntity.ok(RestResponse.success("Kullanıcılar başarıyla listelendi", users));
    }

    // 2. CREATE User
    @PostMapping(path="/create-user")
    public ResponseEntity<RestResponse<User>> createUser(@Valid @RequestBody RegisterRequest registerRequest) {

            User createdUser = userService.createUser(registerRequest);
            return ResponseEntity.ok(RestResponse.success("Kullanıcı başarıyla oluşturuldu.", createdUser));

    }

    // 3. DELETE User
    @DeleteMapping(path="/delete-user/{id}")
    public ResponseEntity<RestResponse<Void>> deleteUser(@PathVariable(name = "id") String id) {

        userService.deleteUser(id);
        return ResponseEntity.ok(RestResponse.success("Kullanıcı başarıyla silindi."));

    }

    // 4. UPDATE User
    @PatchMapping(path = "/update-user/{id}", consumes = "application/json")
    public ResponseEntity<RestResponse<Void>> updateUser(@PathVariable(name = "id") String id, @RequestBody Map<String, Object> updates) {

        userService.updateUser(id, updates);
        return ResponseEntity.ok(RestResponse.success("Kullanıcı bilgileri başarıyla güncellendi."));
    }
}