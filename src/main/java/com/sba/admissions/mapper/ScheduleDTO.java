package com.sba.admissions.mapper;

import com.sba.accounts.pojos.Accounts;
import com.sba.admissions.dto.ScheduleRequestDTO;
import com.sba.admissions.dto.ScheduleResponseDTO;
import com.sba.admissions.pojos.AdmissionSchedules;
import com.sba.enums.ProcessStatus;
import com.sba.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ScheduleDTO {
    private final AccountUtils accountUtils;

    @Autowired
    public ScheduleDTO(AccountUtils accountUtils) {
        this.accountUtils = accountUtils;
    }

    public AdmissionSchedules mapToEntity(ScheduleRequestDTO dto) {
        try{
            Accounts user = accountUtils.getCurrentUser();
            AdmissionSchedules schedule = new AdmissionSchedules();
            schedule.setCreateAt(LocalDateTime.now());
            schedule.setAdmissionAt(dto.getAdmissionAt());
            schedule.setStatus(ProcessStatus.IN_PROCESS);
            schedule.setMeetLink("Wait for link");
            schedule.setUser(user);
            return schedule;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ScheduleResponseDTO mapToResponseDTO(AdmissionSchedules schedule) {
        return new ScheduleResponseDTO(
                schedule.getStaff() != null ? schedule.getStaff().getId() : null,
                schedule.getCreateAt(),
                schedule.getAdmissionAt(),
                schedule.getStatus() != null ? schedule.getStatus().name() : null,
                schedule.getUser() != null ? schedule.getUser().getId() : null,
                schedule.getMeetLink()
        );
    }

}
