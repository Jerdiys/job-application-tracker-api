package com.jerdiys.jobtracker.dtos;

import com.jerdiys.jobtracker.enums.EmploymentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Request object for creating or updating a job posting")
public class JobRequest {
    @NotBlank(message = "Job title is required")
    @Size(min = 3, max = 255, message = "Job title must be between 3 and 255 characters")
    @Schema(description = "Job title", example = "Senior Software Engineer", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @NotBlank(message = "Job description is required")
    @Size(min = 10, message = "Job description must be at least 10 characters")
    @Schema(description = "Detailed job description", example = "We are looking for an experienced software engineer...", requiredMode = Schema.RequiredMode.REQUIRED)
    private String description;

    @NotBlank(message = "Job location is required")
    @Size(max = 255, message = "Job location must not exceed 255 characters")
    @Schema(description = "Job location", example = "San Francisco, CA", requiredMode = Schema.RequiredMode.REQUIRED)
    private String location;

    @NotNull(message = "Employment type is required")
    @Schema(description = "Type of employment", example = "FULL_TIME", requiredMode = Schema.RequiredMode.REQUIRED)
    private EmploymentType employmentType;
}
