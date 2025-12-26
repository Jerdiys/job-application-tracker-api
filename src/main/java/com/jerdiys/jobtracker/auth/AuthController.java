package com.jerdiys.jobtracker.auth;

import com.jerdiys.jobtracker.security.JwtUtil;
import com.jerdiys.jobtracker.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final AuthService authService;

    @Autowired
    public AuthController(AuthenticationManager authManager, JwtUtil jwtUtil,
                          UserDetailsService userDetailsService, AuthService authService) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.authService = authService;
    }

    @PostMapping("/login")
    public @ResponseBody AuthResponse login(@RequestBody AuthRequest authRequest) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getEmail(),
                        authRequest.getPassword()
                )
        );
        UserDetails user = userDetailsService.loadUserByUsername(authRequest.getEmail());
        String jwtToken = jwtUtil.generateToken(user);
        return authService.generateToken(jwtToken);
    }

    @PostMapping("/register")
    public @ResponseBody String register(@RequestBody User user) {
        return authService.registerUser(user);
    }

}
