package com.jerdiys.jobtracker.job;

import com.jerdiys.jobtracker.dtos.JobRequest;
import com.jerdiys.jobtracker.dtos.JobResponse;
import com.jerdiys.jobtracker.enums.EmploymentType;
import com.jerdiys.jobtracker.enums.Role;
import com.jerdiys.jobtracker.exception.ForbiddenException;
import com.jerdiys.jobtracker.exception.ResourceNotFoundException;
import com.jerdiys.jobtracker.user.User;
import com.jerdiys.jobtracker.user.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    private JobRepo jobRepo;

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private JobService jobService;

    private User testRecruiter;
    private Job testJob;
    private JobRequest jobRequest;

    @BeforeEach
    void setUp() {
        testRecruiter = new User();
        testRecruiter.setId(1L);
        testRecruiter.setEmail("recruiter@example.com");
        testRecruiter.setName("Test Recruiter");
        testRecruiter.setRole(Role.ROLE_RECRUITER);

        testJob = new Job();
        testJob.setId(1L);
        testJob.setTitle("Software Engineer");
        testJob.setDescription("Great opportunity");
        testJob.setLocation("San Francisco");
        testJob.setEmploymentType(EmploymentType.FULL_TIME);
        testJob.setRecruiter(testRecruiter);

        jobRequest = JobRequest.builder()
                .title("Software Engineer")
                .description("Great opportunity")
                .location("San Francisco")
                .employmentType(EmploymentType.FULL_TIME)
                .build();
    }

    @Test
    void createJob_WithValidData_ShouldReturnSuccessMessage() {
        // Arrange
        when(userRepo.findByEmail("recruiter@example.com")).thenReturn(Optional.of(testRecruiter));
        when(jobRepo.save(any(Job.class))).thenReturn(testJob);

        // Act
        String result = jobService.createJob(jobRequest, "recruiter@example.com");

        // Assert
        assertEquals("Job created with ID: " + testJob.getId(), result);
        verify(userRepo, times(1)).findByEmail("recruiter@example.com");
        verify(jobRepo, times(1)).save(any(Job.class));
    }

    @Test
    void getAllJobs_ShouldReturnListOfJobs() {
        // Arrange
        Job job2 = new Job();
        job2.setId(2L);
        job2.setTitle("Data Scientist");
        job2.setDescription("ML role");
        job2.setLocation("New York");
        job2.setEmploymentType(EmploymentType.PART_TIME);
        job2.setRecruiter(testRecruiter);

        when(jobRepo.findAll()).thenReturn(Arrays.asList(testJob, job2));

        // Act
        List<JobResponse> result = jobService.getAllJobs();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Software Engineer", result.get(0).getTitle());
        assertEquals("Data Scientist", result.get(1).getTitle());
        verify(jobRepo, times(1)).findAll();
    }

    @Test
    void getJobById_WithValidId_ShouldReturnJob() {
        // Arrange
        when(jobRepo.findById(1L)).thenReturn(Optional.of(testJob));

        // Act
        JobResponse result = jobService.getJobById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("Software Engineer", result.getTitle());
        assertEquals("San Francisco", result.getLocation());
        verify(jobRepo, times(1)).findById(1L);
    }

    @Test
    void getJobById_WithInvalidId_ShouldThrowException() {
        // Arrange
        when(jobRepo.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            jobService.getJobById(999L);
        });
        verify(jobRepo, times(1)).findById(999L);
    }

    @Test
    void updateJob_AsOwner_ShouldUpdateSuccessfully() {
        // Arrange
        when(jobRepo.findById(1L)).thenReturn(Optional.of(testJob));
        when(jobRepo.save(any(Job.class))).thenReturn(testJob);

        JobRequest updateRequest = JobRequest.builder()
                .title("Senior Software Engineer")
                .description("Updated description")
                .location("Remote")
                .employmentType(EmploymentType.FULL_TIME)
                .build();

        // Act
        JobResponse result = jobService.updateJob(1L, updateRequest, "recruiter@example.com");

        // Assert
        assertNotNull(result);
        verify(jobRepo, times(1)).findById(1L);
        verify(jobRepo, times(1)).save(any(Job.class));
    }

    @Test
    void updateJob_AsNonOwner_ShouldThrowForbiddenException() {
        // Arrange
        User otherRecruiter = new User();
        otherRecruiter.setId(2L);
        otherRecruiter.setEmail("other@example.com");

        when(jobRepo.findById(1L)).thenReturn(Optional.of(testJob));

        // Act & Assert
        assertThrows(ForbiddenException.class, () -> {
            jobService.updateJob(1L, jobRequest, "other@example.com");
        });
        verify(jobRepo, times(1)).findById(1L);
        verify(jobRepo, never()).save(any(Job.class));
    }

    @Test
    void deleteJob_AsOwner_ShouldDeleteSuccessfully() {
        // Arrange
        when(jobRepo.findById(1L)).thenReturn(Optional.of(testJob));

        // Act
        String result = jobService.deleteJob(1L, "recruiter@example.com");

        // Assert
        assertEquals("Job deleted successfully.", result);
        verify(jobRepo, times(1)).findById(1L);
        verify(jobRepo, times(1)).delete(testJob);
    }

    @Test
    void deleteJob_AsNonOwner_ShouldThrowForbiddenException() {
        // Arrange
        User otherRecruiter = new User();
        otherRecruiter.setId(2L);
        otherRecruiter.setEmail("other@example.com");

        when(jobRepo.findById(1L)).thenReturn(Optional.of(testJob));

        // Act & Assert
        assertThrows(ForbiddenException.class, () -> {
            jobService.deleteJob(1L, "other@example.com");
        });
        verify(jobRepo, times(1)).findById(1L);
        verify(jobRepo, never()).delete(any(Job.class));
    }
}
