package com.tunahancoban.policy_tracker.controller;

import com.tunahancoban.policy_tracker.model.DTO.request.RegisterRequest;
import com.tunahancoban.policy_tracker.model.DTO.request.UpdateUserRequest;
import com.tunahancoban.policy_tracker.model.enums.Role;
import com.tunahancoban.policy_tracker.model.entity.User;
import com.tunahancoban.policy_tracker.service.interfaces.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/api/user")
@RequiredArgsConstructor
public class RestUserController {

    private final UserService userService;

    // 1. FIND User
    @GetMapping(path= "/with-params")
    public ResponseEntity<Page<User>> getUserWithParam(@RequestParam(name = "id", required = false) String id,
                                                                     @RequestParam(name = "firstName", required = false) String firstName,
                                                                     @RequestParam(name = "lastName", required = false) String lastName,
                                                                     @RequestParam(name = "email", required = false) String email,
                                                                     @RequestParam(name = "role", required = false) Role role,
       @PageableDefault(size = 5, sort = "endDate", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<User> users = userService.getUserWithParam(id, firstName, lastName, email, role, pageable);
        return ResponseEntity.ok(users);
    }

    // 2. CREATE User
    @PostMapping(path="/create-user")
    public ResponseEntity<User> createUser(@Valid @RequestBody RegisterRequest registerRequest) {

            User createdUser = userService.createUser(registerRequest);
            return ResponseEntity.ok(createdUser);

    }

    // 3. DELETE User
    @DeleteMapping(path="/delete-user/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {

        userService.deleteUser(id);
        return ResponseEntity.noContent().build();

    }

    // 4. UPDATE User
    @PatchMapping(path = "/update-user/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody UpdateUserRequest updateUserRequest) {

        User user = userService.updateUser(id, updateUserRequest);
        return ResponseEntity.ok(user);
    }
}