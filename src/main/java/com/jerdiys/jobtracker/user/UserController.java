package com.jerdiys.jobtracker.user;

import com.jerdiys.jobtracker.dtos.UserResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // Only ADMIN can access this endpoint
    public @ResponseBody List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Only ADMIN can access this endpoint
    public @ResponseBody UserResponse getUserById(Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public @ResponseBody UserResponse getCurrentUser(Authentication authentication) {
        String userEmail = authentication.getName();
        return userService.getUserByEmail(userEmail);
    }
}
