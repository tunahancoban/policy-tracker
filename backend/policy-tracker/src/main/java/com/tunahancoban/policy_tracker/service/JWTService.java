package com.tunahancoban.policy_tracker.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
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

@Service
public class JWTService {

    @Value("${jwt.secret}")
    private String secretString;


    private Key secretKey;

    @PostConstruct
    public void init(){
        this.secretKey = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
    }

    //It generates JWT token
    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }

    private <K, V> String generateToken(HashMap<K,V> kvHashMap, UserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER");

        extraClaims.put("role", role);

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername()) // Email is added to subject
                .setIssuedAt(new Date(System.currentTimeMillis())) // Produce date
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7)) // 7 day lifecycle
                .signWith(secretKey, SignatureAlgorithm.HS256) // Signed with key
                .compact();
    }
    //Helper methods

    //Extract claims
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    //Extract email
    public String extractEmail(String token){
        return extractClaim(token, Claims::getSubject);
    }
    //Extract expiration
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    //Extract token expired
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    //Extract is token valid
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractEmail(token); // Get email from token

        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }
}
