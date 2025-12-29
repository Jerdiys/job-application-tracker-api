package com.jerdiys.jobtracker.job;

import com.jerdiys.jobtracker.dtos.JobRequest;
import com.jerdiys.jobtracker.dtos.JobResponse;
import com.jerdiys.jobtracker.exception.ForbiddenException;
import com.jerdiys.jobtracker.exception.ResourceNotFoundException;
import com.jerdiys.jobtracker.user.User;
import com.jerdiys.jobtracker.user.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobService {

    private final JobRepo jobRepo;
    private final UserRepo userRepo;

    @Autowired
    public JobService(JobRepo jobRepo, UserRepo userRepo) {
        this.userRepo = userRepo;
        this.jobRepo = jobRepo;
    }

    public String createJob(JobRequest jobRequest, String userEmail) {
        Job job = new Job();
        User recruiter = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        job.setTitle(jobRequest.getTitle());
        job.setDescription(jobRequest.getDescription());
        job.setLocation(jobRequest.getLocation());
        job.setEmploymentType(jobRequest.getEmploymentType());
        job.setRecruiter(recruiter);

        Job savedJob = jobRepo.save(job);
        return "Job created with ID: " + savedJob.getId();
    }

    public List<JobResponse> getAllJobs() {
        List<Job> jobs = jobRepo.findAll();
        return jobs.stream().map(job -> JobResponse.builder()
                .id(String.valueOf(job.getId()))
                .title(job.getTitle())
                .description(job.getDescription())
                .location(job.getLocation())
                .employmentType(job.getEmploymentType().name())
                .postedBy(job.getRecruiter().getName())
                .build()).toList();
    }

    public JobResponse getJobById(Long id) {
        Job job = jobRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));
        return JobResponse.builder()
                .id(String.valueOf(job.getId()))
                .title(job.getTitle())
                .description(job.getDescription())
                .location(job.getLocation())
                .employmentType(job.getEmploymentType().name())
                .postedBy(job.getRecruiter().getName())
                .build();
    }

    public JobResponse updateJob(Long id, JobRequest jobRequest, String userEmail) {
        Job job = jobRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));
        if (!job.getRecruiter().getEmail().equals(userEmail)) {
            throw new ForbiddenException("Unauthorized to update this job");
        }
        job.setTitle(jobRequest.getTitle());
        job.setDescription(jobRequest.getDescription());
        job.setLocation(jobRequest.getLocation());
        job.setEmploymentType(jobRequest.getEmploymentType());

        Job updatedJob = jobRepo.save(job);
        return JobResponse.builder()
                .id(String.valueOf(updatedJob.getId()))
                .title(updatedJob.getTitle())
                .description(updatedJob.getDescription())
                .location(updatedJob.getLocation())
                .employmentType(updatedJob.getEmploymentType().name())
                .postedBy(updatedJob.getRecruiter().getName())
                .build();
    }

    public String deleteJob(Long id, String userEmail) {
        Job job = jobRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));
        if (!job.getRecruiter().getEmail().equals(userEmail)) {
            throw new ForbiddenException("Unauthorized to delete this job");
        }
        jobRepo.delete(job);
        return "Job deleted successfully.";
    }
}
