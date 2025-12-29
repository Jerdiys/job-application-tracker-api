package com.jerdiys.jobtracker.user;

import com.jerdiys.jobtracker.dtos.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "Endpoints for managing users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get All Users", description = "Retrieve a list of all users in the system.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of users")
    @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // Only ADMIN can access this endpoint
    public @ResponseBody List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @Operation(summary = "Get User by ID", description = "Retrieve detailed information about a specific user. Only accessible by administrators.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved user details")
    @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    @ApiResponse(responseCode = "404", description = "User not found")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Only ADMIN can access this endpoint
    public @ResponseBody UserResponse getUserById(
            @Parameter(description = "ID of the user", required = true) @PathVariable Long id) {
        return userService.getUserById(id);
    }

    @Operation(summary = "Get Current User", description = "Retrieve information about the currently authenticated user.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved current user details")
    @ApiResponse(responseCode = "401", description = "User not authenticated")
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public @ResponseBody UserResponse getCurrentUser(Authentication authentication) {
        String userEmail = authentication.getName();
        return userService.getUserByEmail(userEmail);
    }
}
