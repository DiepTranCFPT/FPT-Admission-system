package com.sba.admissions.service;

import com.sba.admissions.dto.ScheduleRequestDTO;
import com.sba.admissions.dto.ScheduleResponseDTO;
import com.sba.admissions.pojos.AdmissionSchedules;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleService {
    ScheduleResponseDTO createSchedule(LocalDateTime AdmissionAt);
    AdmissionSchedules getScheduleById(String id);
    List<AdmissionSchedules> getAllSchedules();
    ScheduleResponseDTO updateSchedule(String id, ScheduleRequestDTO schedule);
    void deleteSchedule(String id);
    ScheduleResponseDTO respontStaff(String googleMeetLink, String scheduleId);
}
