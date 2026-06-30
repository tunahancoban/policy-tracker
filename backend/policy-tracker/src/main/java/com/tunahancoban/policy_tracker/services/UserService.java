package com.tunahancoban.policy_tracker.services;

import com.tunahancoban.policy_tracker.model.User;
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

    public List<User> getUserWithParam(String id ,String firstName, String lastName,String email , String role){
        User searchCriteria = new User();
        searchCriteria.setId(id);
        searchCriteria.setFirstName(firstName);
        searchCriteria.setLastName(lastName);
        searchCriteria.setEmail(email);
        searchCriteria.setRole(role);

        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();

        Example<User> example = Example.of(searchCriteria, matcher);

        return userRepository.findAll(example);
    }

    public User createUser(User user){

        if(userRepository.existsByEmail(user.getEmail())){
            throw new RuntimeException("This email already used by someone");
        }
        String rawPassword = user.getPassword();
        String hashedPassword = passwordEncoder.encode(rawPassword);
        user.setPassword(hashedPassword);

        return userRepository.save(user);
    }

    public void deleteUser(String id){
        if (!userRepository.existsById(id)){
            throw new RuntimeException("This id does not exist. ID: " +id);
        }
        userRepository.deleteById(id);
    }

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
