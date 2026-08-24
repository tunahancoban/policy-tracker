package com.tunahancoban.policy_tracker.init;

import com.tunahancoban.policy_tracker.model.DTO.events.UserEvent;
import com.tunahancoban.policy_tracker.model.entity.User;
import com.tunahancoban.policy_tracker.model.enums.EventTypes;
import com.tunahancoban.policy_tracker.model.enums.Role;
import com.tunahancoban.policy_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.findByEmail("admin@policytracker.com").isEmpty()) {
            User admin = new User();
            admin.setFullName("Admin");
            admin.setEmail("admin@policytracker.com");
            admin.setPassword(passwordEncoder.encode("1234"));
            admin.setIsActive(true);
            admin.setRole(Role.ROLE_ADMIN);

            userRepository.save(admin);
            eventPublisher.publishEvent(UserEvent.from(admin, EventTypes.CREATE));
        }
    }
}