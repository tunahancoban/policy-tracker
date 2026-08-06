package com.tunahancoban.policy_tracker.service.interfaces;

import com.tunahancoban.policy_tracker.model.DTO.request.RegisterRequest;
import com.tunahancoban.policy_tracker.model.DTO.request.UpdateUserRequest;
import com.tunahancoban.policy_tracker.model.entity.User;
import com.tunahancoban.policy_tracker.model.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserServiceImp {

    Page<User> getUserWithParam(String id, String firstName, String lastName, String email, Role role, Pageable pageable);
    User createUser(RegisterRequest registerRequest);
    void deleteUser(String id);
    User updateUser(String id, UpdateUserRequest request);
    User getUserByEmail(String email);
}
