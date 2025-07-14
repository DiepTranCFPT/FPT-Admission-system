package com.sba.campuses.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CampusRequest {
    private String name;

    private String address;

    private String phone;

    private String email;
}
