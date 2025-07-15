package com.sba.applications.dto;

import com.sba.enums.ApplicationStatus;
import lombok.Data;

@Data
public class ApplicationDTO {
    private String userName;
    private String major;
    private String campus;
    private String scholarship;
    private ApplicationStatus applicationStatus;
}
