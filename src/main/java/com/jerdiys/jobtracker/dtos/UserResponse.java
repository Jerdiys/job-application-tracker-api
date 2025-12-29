package com.jerdiys.jobtracker.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Response object containing user details")
public class UserResponse {
    @Schema(description = "Unique user identifier", example = "1")
    private String id;

    @Schema(description = "User's full name", example = "John Doe")
    private String name;

    @Schema(description = "User's email address", example = "john.doe@example.com")
    private String email;

    @Schema(description = "User's role in the system", example = "ROLE_CANDIDATE")
    private String role;
}
