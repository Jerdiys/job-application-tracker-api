package com.jerdiys.jobtracker.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JobResponse {
    private String id;
    private String title;
    private String description;
    private String location;
    private String employmentType;
    private String postedBy;
}
