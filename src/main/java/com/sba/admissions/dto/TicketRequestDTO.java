package com.sba.admissions.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketRequestDTO {
    private String staffId;
    private LocalDateTime createAt;
    private String topic;
    private String content;
    private String response;
    private String status;
    private String userId;
}
