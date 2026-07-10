package com.tunahancoban.policy_tracker.service;

import com.tunahancoban.policy_tracker.model.DTO.request.RegisterRequest;
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
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues().withIgnoreCase().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<User> example = Example.of(searchCriteria, matcher);

        return userRepository.findAll(example);
    }

    public User createUser(RegisterRequest registerRequest){
        //Checks user is exist
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
    public void updateUser(String id , Map<String, Object> updates){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("This id does not exist. ID: " + id));

        updates.forEach((key, value) -> {
            switch (key) {
                case "email":
                    String newEmail = (String) value;
                    if (userRepository.existsByEmailAndIdNot(newEmail, id)) {
                        throw new RuntimeException("This email is taken");
                    }
                    user.setEmail(newEmail);
                    break;

                case "firstName":
                    user.setFirstName((String) value);
                    break;

                case "lastName":
                    user.setLastName((String) value);
                    break;

                case "password":
                    String rawPassword = (String) value;
                    if (rawPassword != null && !rawPassword.trim().isEmpty()) {
                        user.setPassword(passwordEncoder.encode(rawPassword));
                    }
                    break;
            }
        });
        userRepository.save(user);
    }

}
