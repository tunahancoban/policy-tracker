package com.tunahancoban.policy_tracker.service;

import com.tunahancoban.policy_tracker.model.DTO.request.LoginRequest;
import com.tunahancoban.policy_tracker.model.DTO.response.LoginResponse;
import com.tunahancoban.policy_tracker.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;

    public LoginResponse authenticate(LoginRequest request) {
        User user = userService.getUserByEmail( request.getEmail());

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ya da şifre yanlış!");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ya da şifre yanlış!");
        }

        return new LoginResponse(jwtService.generateToken(user), user.getRole().toString(), user.getEmail());
    }

    public LoginResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Oturum bulunamadı");
        }

        String email = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER");

        return new LoginResponse(role, email);
    }
}