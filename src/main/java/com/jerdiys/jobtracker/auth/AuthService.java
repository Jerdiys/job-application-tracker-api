package com.jerdiys.jobtracker.auth;

import com.jerdiys.jobtracker.user.User;
import com.jerdiys.jobtracker.user.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.userRepo = userRepo;
    }

    public String registerUser(User user) {
        if (userRepo.findByEmail(user.getEmail()).isPresent()) {
            return "Error: Email is already in use.";
        }
        String password = user.getPassword();
        user.setPassword(passwordEncoder.encode(password));
        userRepo.save(user);
        return "User registered successfully." ;
    }

    public AuthResponse generateToken(String token) {
        return AuthResponse.builder()
                .token(token)
                .build();
    }

}
