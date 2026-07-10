package com.tunahancoban.policy_tracker.service;

import com.tunahancoban.policy_tracker.model.DTO.request.LoginRequest;
import com.tunahancoban.policy_tracker.model.DTO.response.LoginResponse;
import com.tunahancoban.policy_tracker.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;


    public LoginResponse authenticate(LoginRequest request){

        //Is this e-mail exist
        List<User> userList = userService.getUserWithParam(null,null,null,request.getEmail(),null);

        //If it is not throw an exception
        if (userList == null || userList.isEmpty()){
            throw new RuntimeException("E-mail ya da şifre yanlış!");
        }

        User user = userList.getFirst();
        //If email exist, compare passwords
        if (passwordEncoder.matches(request.getPassword(), user.getPassword())){
            //If they are matching return a login response with jwt token
            return new LoginResponse(jwtService.generateToken(user), user.getRole().toString(), user.getEmail());
        }else{
            //If they are not matching throw an exception
            throw new RuntimeException("E-mail ya da şifre yanlış!");
        }
    }

    public LoginResponse getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {
            throw new RuntimeException("Oturum bulunamadı");
        }
        String email = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER");
        return new LoginResponse(role, email);
    }
}
