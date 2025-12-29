package com.jerdiys.jobtracker.job;

import com.jerdiys.jobtracker.dtos.JobRequest;
import com.jerdiys.jobtracker.dtos.JobResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@Tag(name = "Job Management", description = "Endpoints for managing job postings")
public class JobController {

    private final JobService jobService;

    @Autowired
    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @Operation(summary = "Create Job", description = "Create a new job posting. Only accessible by recruiters.")
    @ApiResponse(responseCode = "200", description = "Job created successfully")
    @ApiResponse(responseCode = "403", description = "Access denied - Recruiter role required")
    @PostMapping
    @PreAuthorize("hasRole('RECRUITER')")
    public @ResponseBody String createJob(@Valid @RequestBody JobRequest jobRequest, Authentication auth) {
        String userEmail = auth.getName();
        return jobService.createJob(jobRequest, userEmail);
    }

    @Operation(summary = "Get All Jobs", description = "Retrieve a list of all job postings available in the system.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of jobs")
    @GetMapping
    public @ResponseBody List<JobResponse> getJobs() {
        return jobService.getAllJobs();
    }

    @Operation(summary = "Get Job by ID", description = "Retrieve detailed information about a specific job posting.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved job details")
    @ApiResponse(responseCode = "404", description = "Job not found")
    @GetMapping("/{id}")
    public @ResponseBody JobResponse getJobById(
            @Parameter(description = "ID of the job", required = true) @PathVariable Long id) {
        return jobService.getJobById(id);
    }

    @Operation(summary = "Update Job", description = "Update an existing job posting. Only accessible by recruiters who own the job.")
    @ApiResponse(responseCode = "200", description = "Job updated successfully")
    @ApiResponse(responseCode = "403", description = "Access denied - Recruiter role required or not job owner")
    @ApiResponse(responseCode = "404", description = "Job not found")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('RECRUITER')")
    public @ResponseBody JobResponse updateJob(
            @Parameter(description = "ID of the job to update", required = true) @PathVariable Long id,
            @Valid @RequestBody JobRequest jobRequest,
            Authentication auth) {
        String userEmail = auth.getName();
        return jobService.updateJob(id, jobRequest, userEmail);
    }

    @Operation(summary = "Delete Job", description = "Delete a job posting. Only accessible by recruiters who own the job.")
    @ApiResponse(responseCode = "200", description = "Job deleted successfully")
    @ApiResponse(responseCode = "403", description = "Access denied - Recruiter role required or not job owner")
    @ApiResponse(responseCode = "404", description = "Job not found")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('RECRUITER')")
    public @ResponseBody String deleteJob(
            @Parameter(description = "ID of the job to delete", required = true) @PathVariable Long id,
            Authentication auth) {
        String userEmail = auth.getName();
        return jobService.deleteJob(id, userEmail);
    }

}
