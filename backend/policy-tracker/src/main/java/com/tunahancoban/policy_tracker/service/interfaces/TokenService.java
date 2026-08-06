package com.tunahancoban.policy_tracker.service.interfaces;

import org.springframework.security.core.userdetails.UserDetails;

public interface TokenService {

    String generateToken(UserDetails userDetails);
    String extractEmail(String token);
    boolean isTokenValid(String token, UserDetails userDetails);
}
