package com.tunahancoban.policy_tracker.controller;

import com.tunahancoban.policy_tracker.model.DTO.request.auth.RegisterRequest;
import com.tunahancoban.policy_tracker.model.DTO.request.user.UpdateUserRequest;
import com.tunahancoban.policy_tracker.model.DTO.request.user.UserSearchRequest;
import com.tunahancoban.policy_tracker.model.entity.User;
import com.tunahancoban.policy_tracker.service.UserSearchService;
import com.tunahancoban.policy_tracker.service.interfaces.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/api/users")
@RequiredArgsConstructor
public class RestUserController {

    private final UserService userService;
    private final UserSearchService searchService;

    // 1. FIND User
    @GetMapping
    public ResponseEntity<Page<User>> getUserWithParam(UserSearchRequest searchRequest) {

        Page<User> users = searchService.search(searchRequest);
        return ResponseEntity.ok(users);
    }

    // 2. CREATE User
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody RegisterRequest registerRequest) {

        User createdUser = userService.createUser(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);

    }

    // 3. DELETE User
    @DeleteMapping(path="/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {

        userService.deleteUser(id);
        return ResponseEntity.noContent().build();

    }

    // 4. UPDATE User
    @PatchMapping(path = "/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody UpdateUserRequest updateUserRequest) {

        User user = userService.updateUser(id, updateUserRequest);
        return ResponseEntity.ok(user);
    }
}