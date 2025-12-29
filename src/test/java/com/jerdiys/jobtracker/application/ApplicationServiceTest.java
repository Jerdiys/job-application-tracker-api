package com.jerdiys.jobtracker.application;

import com.jerdiys.jobtracker.dtos.ApplicationResponse;
import com.jerdiys.jobtracker.enums.ApplicationStatus;
import com.jerdiys.jobtracker.enums.EmploymentType;
import com.jerdiys.jobtracker.enums.Role;
import com.jerdiys.jobtracker.exception.ForbiddenException;
import com.jerdiys.jobtracker.exception.ResourceNotFoundException;
import com.jerdiys.jobtracker.job.Job;
import com.jerdiys.jobtracker.job.JobRepo;
import com.jerdiys.jobtracker.user.User;
import com.jerdiys.jobtracker.user.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private ApplicationRepo applicationRepo;

    @Mock
    private JobRepo jobRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ApplicationService applicationService;

    private User candidate;
    private User recruiter;
    private Job testJob;
    private Application testApplication;

    @BeforeEach
    void setUp() {
        candidate = new User();
        candidate.setId(1L);
        candidate.setEmail("candidate@example.com");
        candidate.setName("Test Candidate");
        candidate.setRole(Role.ROLE_CANDIDATE);

        recruiter = new User();
        recruiter.setId(2L);
        recruiter.setEmail("recruiter@example.com");
        recruiter.setName("Test Recruiter");
        recruiter.setRole(Role.ROLE_RECRUITER);

        testJob = new Job();
        testJob.setId(1L);
        testJob.setTitle("Software Engineer");
        testJob.setDescription("Great opportunity");
        testJob.setLocation("San Francisco");
        testJob.setEmploymentType(EmploymentType.FULL_TIME);
        testJob.setRecruiter(recruiter);

        testApplication = new Application();
        testApplication.setId(1L);
        testApplication.setJob(testJob);
        testApplication.setCandidate(candidate);
        testApplication.setStatus(ApplicationStatus.APPLIED);
        testApplication.setAppliedAt(LocalDateTime.now());
    }

    @Test
    void applyToJob_WithValidData_ShouldCreateApplication() {
        // Arrange
        when(authentication.getName()).thenReturn("candidate@example.com");
        when(userRepo.findByEmail("candidate@example.com")).thenReturn(Optional.of(candidate));
        when(jobRepo.findById(1L)).thenReturn(Optional.of(testJob));
        when(applicationRepo.existsByJobAndCandidate(any(Job.class), any(User.class))).thenReturn(false);
        when(applicationRepo.save(any(Application.class))).thenReturn(testApplication);

        // Act
        ApplicationResponse result = applicationService.applyToJob(authentication, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getApplicationId());
        assertEquals("Test Candidate", result.getCandidate());
        verify(applicationRepo, times(1)).save(any(Application.class));
    }

    @Test
    void applyToJob_WhenAlreadyApplied_ShouldThrowException() {
        // Arrange
        when(authentication.getName()).thenReturn("candidate@example.com");
        when(userRepo.findByEmail("candidate@example.com")).thenReturn(Optional.of(candidate));
        when(jobRepo.findById(1L)).thenReturn(Optional.of(testJob));
        when(applicationRepo.existsByJobAndCandidate(any(Job.class), any(User.class))).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            applicationService.applyToJob(authentication, 1L);
        });
        verify(applicationRepo, never()).save(any(Application.class));
    }

    @Test
    void getMyApplications_ShouldReturnCandidateApplications() {
        // Arrange
        when(authentication.getName()).thenReturn("candidate@example.com");
        when(userRepo.findByEmail("candidate@example.com")).thenReturn(Optional.of(candidate));
        when(applicationRepo.findByCandidate(any(User.class))).thenReturn(Arrays.asList(testApplication));

        // Act
        List<ApplicationResponse> result = applicationService.getMyApplications(authentication);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Candidate", result.get(0).getCandidate());
        verify(applicationRepo, times(1)).findByCandidate(any(User.class));
    }

    @Test
    void getAllApplications_ShouldReturnAllApplications() {
        // Arrange
        when(applicationRepo.findAll()).thenReturn(Arrays.asList(testApplication));

        // Act
        List<ApplicationResponse> result = applicationService.getAllApplications();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(applicationRepo, times(1)).findAll();
    }

    @Test
    void getApplicationsForJob_AsRecruiterOwner_ShouldReturnApplications() {
        // Arrange
        when(authentication.getName()).thenReturn("recruiter@example.com");
        when(jobRepo.findById(1L)).thenReturn(Optional.of(testJob));
        when(userRepo.findByEmail("recruiter@example.com")).thenReturn(Optional.of(recruiter));
        when(applicationRepo.findByJob(any(Job.class))).thenReturn(Arrays.asList(testApplication));

        // Act
        List<ApplicationResponse> result = applicationService.getApplicationsForJob(1L, authentication);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(applicationRepo, times(1)).findByJob(any(Job.class));
    }

    @Test
    void getApplicationsForJob_AsNonOwner_ShouldThrowException() {
        // Arrange
        User otherRecruiter = new User();
        otherRecruiter.setId(3L);
        otherRecruiter.setEmail("other@example.com");
        otherRecruiter.setRole(Role.ROLE_RECRUITER);

        when(authentication.getName()).thenReturn("other@example.com");
        when(jobRepo.findById(1L)).thenReturn(Optional.of(testJob));
        when(userRepo.findByEmail("other@example.com")).thenReturn(Optional.of(otherRecruiter));

        // Act & Assert
        assertThrows(ForbiddenException.class, () -> {
            applicationService.getApplicationsForJob(1L, authentication);
        });
        verify(applicationRepo, never()).findByJob(any(Job.class));
    }

    @Test
    void updateApplicationStatus_AsRecruiter_ShouldUpdateStatus() {
        // Arrange
        when(authentication.getName()).thenReturn("recruiter@example.com");
        when(applicationRepo.findById(1L)).thenReturn(Optional.of(testApplication));
        when(userRepo.findByEmail("recruiter@example.com")).thenReturn(Optional.of(recruiter));
        when(applicationRepo.save(any(Application.class))).thenReturn(testApplication);

        // Act
        ApplicationResponse result = applicationService.updateApplicationStatus(
                1L, ApplicationStatus.REVIEWING, authentication);

        // Assert
        assertNotNull(result);
        verify(applicationRepo, times(1)).save(any(Application.class));
    }

    @Test
    void updateApplicationStatus_WithInvalidId_ShouldThrowException() {
        // Arrange
        when(authentication.getName()).thenReturn("recruiter@example.com");
        when(userRepo.findByEmail("recruiter@example.com")).thenReturn(Optional.of(recruiter));
        when(applicationRepo.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            applicationService.updateApplicationStatus(999L, ApplicationStatus.REVIEWING, authentication);
        });
        verify(applicationRepo, never()).save(any(Application.class));
    }
}
