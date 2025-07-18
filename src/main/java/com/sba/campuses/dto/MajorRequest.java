package com.sba.campuses.dto;


import com.sba.campuses.pojos.Campus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MajorRequest {
    private String name;

    private String description;

    private Double duration;

    private  Double fee;


}
