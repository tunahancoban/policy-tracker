package com.tunahancoban.policy_tracker.service;

import com.tunahancoban.policy_tracker.model.DTO.request.auth.LoginRequest;
import com.tunahancoban.policy_tracker.model.DTO.response.LoginResponse;
import com.tunahancoban.policy_tracker.model.entity.User;
import com.tunahancoban.policy_tracker.repository.UserRepository;
import com.tunahancoban.policy_tracker.service.interfaces.AuthService;
import com.tunahancoban.policy_tracker.service.interfaces.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthServiceImp implements AuthService {
    private final PasswordEncoder passwordEncoder;
    private final TokenService jwtService;
    private final UserRepository userRepository;

    @Override
    public LoginResponse authenticate(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow((() ->
         new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ya da şifre yanlış!")));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ya da şifre yanlış!");
        }

        return new LoginResponse(jwtService.generateToken(user), user.getRole().toString(), user.getId(),user.getEmail());
    }

    @Override
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
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Kullanıcı bulunamadı"));

        return new LoginResponse(role, user.getId(), email);
    }
}