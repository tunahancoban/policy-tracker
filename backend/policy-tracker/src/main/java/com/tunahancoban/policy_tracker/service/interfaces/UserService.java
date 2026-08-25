package com.tunahancoban.policy_tracker.service.interfaces;

import com.tunahancoban.policy_tracker.model.DTO.request.auth.RegisterRequest;
import com.tunahancoban.policy_tracker.model.DTO.request.user.UpdateUserRequest;
import com.tunahancoban.policy_tracker.model.entity.User;
import com.tunahancoban.policy_tracker.model.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    Page<User> getUserWithParam(String id, String firstName, String email, Role role, Pageable pageable);
    User createUser(RegisterRequest registerRequest);
    void deleteUser(String id);
    User updateUser(String id, UpdateUserRequest request);
    User getUserByEmail(String email);
}
