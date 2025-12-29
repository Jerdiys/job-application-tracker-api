package com.jerdiys.jobtracker.auth;

import com.jerdiys.jobtracker.dtos.UserResponse;
import com.jerdiys.jobtracker.security.JwtUtil;
import com.jerdiys.jobtracker.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
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

    @Operation(summary = "User Login", description = "Authenticate a user with email and password, returns a JWT token upon successful authentication.")
    @ApiResponse(responseCode = "200", description = "Successfully authenticated, JWT token returned")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    @PostMapping("/login")
    public @ResponseBody AuthResponse login(@Valid @RequestBody AuthRequest authRequest) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getEmail(),
                        authRequest.getPassword()));
        UserDetails user = userDetailsService.loadUserByUsername(authRequest.getEmail());
        String jwtToken = jwtUtil.generateToken(user);
        return authService.generateToken(jwtToken);
    }

    @Operation(summary = "User Registration", description = "Register a new user account in the system.")
    @ApiResponse(responseCode = "200", description = "User registered successfully")
    @ApiResponse(responseCode = "400", description = "Invalid user data or email already exists")
    @PostMapping("/register")
    public @ResponseBody UserResponse register(@RequestBody User user) {
        return authService.registerUser(user);
    }

}
