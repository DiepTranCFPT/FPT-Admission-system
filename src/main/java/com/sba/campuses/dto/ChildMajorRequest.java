package com.sba.campuses.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder

public class ChildMajorRequest {

    private String name;
    private String description;
    private Double duration;
    private Double fee;
    private String parentMajorId;

}
