package com.tunahancoban.policy_tracker.service;

import com.tunahancoban.policy_tracker.mapper.UserMapper;
import com.tunahancoban.policy_tracker.model.DTO.request.RegisterRequest;
import com.tunahancoban.policy_tracker.model.DTO.request.UpdateUserRequest;
import com.tunahancoban.policy_tracker.model.enums.Role;
import com.tunahancoban.policy_tracker.model.entity.User;
import com.tunahancoban.policy_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public Page<User> getUserWithParam(String id, String firstName, String lastName, String email, Role role, Pageable pageable) {
        log.debug("Searching users - id: {}, firstName: {}, lastName: {}, email: {}, role: {}",
                id, firstName, lastName, email, role);

        //Creates a searchCriteria
        User searchCriteria = User.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .role(role)
                .build();

        //Searches the user
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<User> example = Example.of(searchCriteria, matcher);

        Page<User> result = userRepository.findAll(example, pageable);
        log.debug("User search completed - found {} record(s)", result.getTotalElements());

        return result;
    }

    public User createUser(RegisterRequest registerRequest) {
        log.info("Creating user - email: {}, role: {}", registerRequest.getEmail(), registerRequest.getRole());

        //Checks user does exist
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            log.warn("User creation failed - email already in use: {}", registerRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This email already used by someone");
        }

        //It is hashing password
        String rawPassword = registerRequest.getPassword();
        String hashedPassword = passwordEncoder.encode(rawPassword);
        // Note: raw and hashed passwords are intentionally never logged

        //It saves user
        User user = userMapper.toEntity(registerRequest);

        User savedUser = userRepository.save(user);
        log.info("User successfully created - id: {}, email: {}", savedUser.getId(), savedUser.getEmail());

        return savedUser;
    }

    //Delete user
    public void deleteUser(String id) {
        log.info("Deleting user - id: {}", id);

        if (!userRepository.existsById(id)) {
            log.warn("User deletion failed - id not found: {}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This id does not exist. ID: " + id);
        }
        userRepository.deleteById(id);
        log.info("User successfully deleted - id: {}", id);
    }

    //Update User
    public User updateUser(String id, UpdateUserRequest request) {
        log.info("Updating user - id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User update failed - id not found: {}", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "This id does not exist. ID: " + id);
                });

        if (request.getEmail().isPresent()) {
            String newEmail = request.getEmail().get();

            if (newEmail == null || newEmail.isBlank()) {
                log.warn("User update failed - email cannot be empty - id: {}", id);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email cannot be empty");
            }

            if (userRepository.existsByEmailAndIdNot(newEmail, id)) {
                log.warn("User update failed - email already taken: {}", newEmail);
                throw new ResponseStatusException(HttpStatus.CONFLICT, "This email is taken");
            }
        }

        if (request.getRole().isPresent() && request.getRole().get() == null) {
            log.warn("User update failed - role cannot be null - id: {}", id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role cannot be null");
        }

        userMapper.updateEntityFromRequest(request, user);

        if (request.getPassword().isPresent()) {
            String rawPassword = request.getPassword().get();

            if (rawPassword != null && !rawPassword.trim().isEmpty()) {
                user.setPassword(passwordEncoder.encode(rawPassword));
                log.info("Password updated for user - id: {}", id);
            }
        }

        User updatedUser = userRepository.save(user);
        log.info("User successfully updated - id: {}", id);

        return updatedUser;
    }

    public User getUserByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found with email: " + email
                ));
    }

}