package com.jerdiys.jobtracker.dtos;

import com.jerdiys.jobtracker.enums.ApplicationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Response object containing job application details")
public class ApplicationResponse {
    @Schema(description = "Unique identifier for the application", example = "1")
    private Long applicationId;

    @Schema(description = "Job details for this application")
    private JobResponse job;

    @Schema(description = "Email of the candidate who applied", example = "candidate@example.com")
    private String candidate;

    @Schema(description = "Current status of the application", example = "APPLIED")
    private ApplicationStatus status;

    @Schema(description = "Timestamp when the application was submitted", example = "2024-01-15T10:30:00")
    private String appliedAt;
}
