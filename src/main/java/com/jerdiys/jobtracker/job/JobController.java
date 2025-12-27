package com.jerdiys.jobtracker.job;

import com.jerdiys.jobtracker.dtos.JobRequest;
import com.jerdiys.jobtracker.dtos.JobResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    @Autowired
    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping
    @PreAuthorize("hasRole('RECRUITER')")
    public @ResponseBody String createJob(@RequestBody JobRequest jobRequest, Authentication auth) {
        String userEmail = auth.getName();
        return jobService.createJob(jobRequest, userEmail);
    }

    @GetMapping
    public @ResponseBody List<JobResponse> getJobs() {
        return jobService.getAllJobs();
    }

    @GetMapping("/{id}")
    public @ResponseBody JobResponse getJobById(@PathVariable Long id) {
        return jobService.getJobById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('RECRUITER')")
    public @ResponseBody String updateJob(@PathVariable Long id, @RequestBody JobRequest jobRequest, Authentication auth) {
        String userEmail = auth.getName();
        return jobService.updateJob(id, jobRequest, userEmail);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('RECRUITER')")
    public @ResponseBody String deleteJob(@PathVariable Long id, Authentication auth) {
        String userEmail = auth.getName();
        return jobService.deleteJob(id, userEmail);
    }

}
