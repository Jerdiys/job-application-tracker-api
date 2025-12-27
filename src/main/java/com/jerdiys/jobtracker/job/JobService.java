package com.jerdiys.jobtracker.job;

import com.jerdiys.jobtracker.dtos.JobRequest;
import com.jerdiys.jobtracker.dtos.JobResponse;
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
                .orElseThrow(() -> new RuntimeException("User not found"));
        job.setTitle(jobRequest.getTitle());
        job.setDescription(jobRequest.getDescription());
        job.setLocation(jobRequest.getLocation());
        job.setEmploymentType(jobRequest.getEmploymentType());
        job.setRecruiter(recruiter);


        jobRepo.save(job);
        return "Job created successfully.";
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
                .orElseThrow(() -> new RuntimeException("Job not found"));
        return JobResponse.builder()
                .id(String.valueOf(job.getId()))
                .title(job.getTitle())
                .description(job.getDescription())
                .location(job.getLocation())
                .employmentType(job.getEmploymentType().name())
                .postedBy(job.getRecruiter().getName())
                .build();
    }

    public String updateJob(Long id, JobRequest jobRequest, String userEmail) {
        Job job = jobRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        if (!job.getRecruiter().getEmail().equals(userEmail)) {
            throw new RuntimeException("Unauthorized to update this job");
        }
        job.setTitle(jobRequest.getTitle());
        job.setDescription(jobRequest.getDescription());
        job.setLocation(jobRequest.getLocation());
        job.setEmploymentType(jobRequest.getEmploymentType());

        jobRepo.save(job);
        return "Job updated successfully.";
    }

    public String deleteJob(Long id, String userEmail) {
        Job job = jobRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        if (!job.getRecruiter().getEmail().equals(userEmail)) {
            throw new RuntimeException("Unauthorized to delete this job");
        }
        jobRepo.delete(job);
        return "Job deleted successfully.";
    }
}
