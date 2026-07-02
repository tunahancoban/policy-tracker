package com.tunahancoban.policy_tracker.services;

import com.tunahancoban.policy_tracker.model.LoginRequest;
import com.tunahancoban.policy_tracker.model.LoginResponse;
import com.tunahancoban.policy_tracker.model.User;
import lombok.RequiredArgsConstructor;
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

        List<User> userList = userService.getUserWithParam(null,null,null,request.getEmail(),null);

        if (userList == null || userList.isEmpty()){
            throw new RuntimeException("Email or password is wrong");
        }
        User user = userList.getFirst();

        if (passwordEncoder.matches(request.getPassword(), user.getPassword())){
            return new LoginResponse(jwtService.generateToken(user), user.getRole().toString(), user.getEmail());
        }else{
            throw new RuntimeException("Email or password is wrong");
        }
    }
}
