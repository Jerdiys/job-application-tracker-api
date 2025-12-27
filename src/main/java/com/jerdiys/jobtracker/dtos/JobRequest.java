package com.jerdiys.jobtracker.dtos;

import com.jerdiys.jobtracker.enums.EmploymentType;
import com.jerdiys.jobtracker.user.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JobRequest {
    private String title;
    private String description;
    private String location;
    private EmploymentType employmentType;
}
