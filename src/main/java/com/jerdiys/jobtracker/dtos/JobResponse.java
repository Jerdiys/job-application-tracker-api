package com.jerdiys.jobtracker.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Response object containing job posting details")
public class JobResponse {
    @Schema(description = "Unique job identifier", example = "1")
    private String id;

    @Schema(description = "Job title", example = "Senior Software Engineer")
    private String title;

    @Schema(description = "Job description", example = "We are looking for an experienced software engineer...")
    private String description;

    @Schema(description = "Job location", example = "San Francisco, CA")
    private String location;

    @Schema(description = "Type of employment", example = "FULL_TIME")
    private String employmentType;

    @Schema(description = "Email of the recruiter who posted the job", example = "recruiter@example.com")
    private String postedBy;
}
