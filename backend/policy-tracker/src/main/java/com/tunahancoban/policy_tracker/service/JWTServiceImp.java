package com.tunahancoban.policy_tracker.service;

import com.tunahancoban.policy_tracker.service.interfaces.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
public class JWTServiceImp implements TokenService {

    @Value("${jwt.secret}")
    private String secretString;

    private Key secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
        log.info("JWT signing key initialized");
    }

    //It generates JWT token
    @Override
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    private <K, V> String generateToken(HashMap<K, V> kvHashMap, UserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER");
        extraClaims.put("role", role);

        String token = Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername()) // Email is added to subject
                .setIssuedAt(new Date(System.currentTimeMillis())) // Produce date
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7)) // 7 day lifecycle
                .signWith(secretKey, SignatureAlgorithm.HS256) // Signed with key
                .compact();

        // Never log the token itself - log only non-sensitive metadata
        log.info("JWT token generated for user: {} with role: {}", userDetails.getUsername(), role);

        return token;
    }

    //Helper methods
    //Extract claims
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.warn("Attempted to parse an expired JWT token");
            throw e;
        } catch (JwtException e) {
            log.warn("Invalid JWT token encountered: {}", e.getMessage());
            throw e;
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    //Extract email
    @Override
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    //Extract expiration
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    //Extract token expired
    private boolean isTokenExpired(String token) {
        boolean expired = extractExpiration(token).before(new Date());
        if (expired) {
            log.debug("Token is expired");
        }
        return expired;
    }

    //Extract is token valid
    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractEmail(token); // Get email from token
        boolean valid = username.equals(userDetails.getUsername()) && !isTokenExpired(token);

        if (!valid) {
            log.warn("Token validation failed for user: {}", userDetails.getUsername());
        } else {
            log.debug("Token successfully validated for user: {}", username);
        }

        return valid;
    }
}