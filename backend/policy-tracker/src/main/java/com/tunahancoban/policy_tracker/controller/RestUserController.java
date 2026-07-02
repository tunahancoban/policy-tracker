package com.tunahancoban.policy_tracker.controller;

import com.tunahancoban.policy_tracker.model.RestResponse;
import com.tunahancoban.policy_tracker.model.Role;
import com.tunahancoban.policy_tracker.model.User;
import com.tunahancoban.policy_tracker.services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public RestResponse<List<User>> getUserWithParam(@RequestParam(name = "id", required = false) String id,
                                                     @RequestParam(name = "firstName", required = false) String firstName,
                                                     @RequestParam(name = "lastName", required = false) String lastName,
                                                     @RequestParam(name = "email", required = false) String email,
                                                     @RequestParam(name = "role", required = false) Role role) {
        try {
            List<User> users = userService.getUserWithParam(id, firstName, lastName, email, role);
            return RestResponse.success("Users are listed successfully.", users);
        } catch (Exception e) {
            return RestResponse.error("An error occurred while listing users.: " + e.getMessage());
        }
    }

    // 2. CREATE User
    @PostMapping(path="/create-user")
    public RestResponse<User> createUser(@Valid @RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            return RestResponse.success("Kullanıcı başarıyla oluşturuldu.", createdUser);
        } catch (Exception e) {
            return RestResponse.error("Kullanıcı oluşturulurken hata: " + e.getMessage());
        }
    }

    // 3. DELETE User
    @DeleteMapping(path="/delete-user/{id}")
    public RestResponse<Void> deleteUser(@PathVariable(name = "id") String id) {
        try {
            userService.deleteUser(id);
            return RestResponse.success("Kullanıcı başarıyla silindi.");
        } catch (Exception e) {
            return RestResponse.error("Kullanıcı silinirken hata: " + e.getMessage());
        }
    }

    // 4. UPDATE User
    @PatchMapping(path = "/update-user/{id}", consumes = "application/json")
    public RestResponse<Void> updateUser(@PathVariable(name = "id") String id, @RequestBody Map<String, Object> updates) {
        try {
            userService.updateUser(id, updates);
            return RestResponse.success("Kullanıcı bilgileri başarıyla güncellendi.");
        } catch (Exception e) {
            return RestResponse.error("Kullanıcı güncellenirken hata: " + e.getMessage());
        }
    }
}