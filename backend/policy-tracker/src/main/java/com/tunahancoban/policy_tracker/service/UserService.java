package com.tunahancoban.policy_tracker.service;

import com.tunahancoban.policy_tracker.annotation.LogActivity;
import com.tunahancoban.policy_tracker.model.DTO.request.RegisterRequest;
import com.tunahancoban.policy_tracker.model.entity.Policy;
import com.tunahancoban.policy_tracker.model.enums.Role;
import com.tunahancoban.policy_tracker.model.entity.User;
import com.tunahancoban.policy_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public List<User> getUserWithParam(String id ,String firstName, String lastName,String email , Role role){
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

        return userRepository.findAll(example);
    }

    public User createUser(RegisterRequest registerRequest){
        //Checks user does exist
        if(userRepository.existsByEmail(registerRequest.getEmail())){
            throw new RuntimeException("This email already used by someone");
        }

        //It is hashing password
        String rawPassword = registerRequest.getPassword();
        String hashedPassword = passwordEncoder.encode(rawPassword);

        //It saves user
        User user = User.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .password(hashedPassword)
                .role(registerRequest.getRole())
                .build();

        return userRepository.save(user);
    }

    //Delete user
    public void deleteUser(String id){
        if (!userRepository.existsById(id)){
            throw new RuntimeException("This id does not exist. ID: " +id);
        }
        userRepository.deleteById(id);
    }

    //Update User
    public User updateUser(String id , Map<String, Object> updates){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("This id does not exist. ID: " + id));

        User.UserBuilder userBuilder = user.toBuilder();

        updates.forEach((key, value) -> {
            switch (key) {
                case "email":
                    String newEmail = (String) value;
                    if (userRepository.existsByEmailAndIdNot(newEmail, id)) {
                        throw new RuntimeException("This email is taken");
                    }
                    userBuilder.email(newEmail);
                    break;

                case "firstName":
                    userBuilder.firstName((String) value);
                    break;

                case "lastName":
                    userBuilder.lastName((String) value);
                    break;

                case "password":
                    String rawPassword = (String) value;
                    if (rawPassword != null && !rawPassword.trim().isEmpty()) {
                        userBuilder.password(passwordEncoder.encode(rawPassword));
                    }
                    break;
                case "role":
                    userBuilder.role(Role.valueOf((String) value));
                    break;
            }
        });
        User updatedUser = userBuilder.build();
        userRepository.save(updatedUser);
        return updatedUser;
    }

}
