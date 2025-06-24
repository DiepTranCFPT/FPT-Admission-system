package com.sba.admissions.service;

import com.sba.admissions.dto.ScheduleRequestDTO;
import com.sba.admissions.dto.ScheduleResponseDTO;
import com.sba.admissions.pojos.AdmissionSchedules;
import java.util.List;
import java.util.Optional;

public interface ScheduleService {
    ScheduleResponseDTO createSchedule(ScheduleRequestDTO schedule);
    ScheduleResponseDTO getScheduleById(String id);
    List<ScheduleResponseDTO> getAllSchedules();
    ScheduleResponseDTO updateSchedule(String id, ScheduleRequestDTO schedule);
    void deleteSchedule(String id);

    ScheduleResponseDTO respontStaff(String googleMeetLink, String scheduleId);
}
