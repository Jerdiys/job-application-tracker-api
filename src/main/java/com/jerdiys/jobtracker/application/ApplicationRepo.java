package com.jerdiys.jobtracker.application;

import com.jerdiys.jobtracker.job.Job;
import com.jerdiys.jobtracker.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepo extends JpaRepository<Application, Long> {
    List<Application> findByCandidate(User candidate);
    List<Application> findByJob(Job job);
}
