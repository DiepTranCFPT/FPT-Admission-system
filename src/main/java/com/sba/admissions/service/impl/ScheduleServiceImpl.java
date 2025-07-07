package com.sba.admissions.service.impl;

import com.sba.accounts.pojos.Accounts;
import com.sba.admissions.dto.ScheduleRequestDTO;
import com.sba.admissions.dto.ScheduleResponseDTO;
import com.sba.admissions.pojos.AdmissionSchedules;
import com.sba.admissions.repository.ScheduleRepository;
import com.sba.admissions.service.ScheduleService;
import com.sba.enums.ProcessStatus;
import com.sba.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class ScheduleServiceImpl implements ScheduleService {
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private AccountUtils accountUtils;

    private AdmissionSchedules mapToEntity(ScheduleRequestDTO dto) {
        Accounts user = accountUtils.getCurrentUser();
        AdmissionSchedules schedule = new AdmissionSchedules();
        schedule.setCreateAt(LocalDateTime.now());
        schedule.setAdmissionAt(dto.getAdmissionAt());
        schedule.setStatus(ProcessStatus.IN_PROCESS);
        schedule.setMeetLink("Wait for link");
        schedule.setUser(user);
        return schedule;
    }

    private ScheduleResponseDTO mapToResponseDTO(AdmissionSchedules schedule) {
        return new ScheduleResponseDTO(
            schedule.getStaff() != null ? schedule.getStaff().getId() : null,
            schedule.getCreateAt(),
            schedule.getAdmissionAt(),
            schedule.getStatus() != null ? schedule.getStatus().name() : null,
            schedule.getUser() != null ? schedule.getUser().getId() : null,
            schedule.getMeetLink()
        );
    }
//dang ky tu van bang gg meet
    @Override
    public ScheduleResponseDTO createSchedule(LocalDateTime admissionAt) {
        if(admissionAt == null || admissionAt.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Admission date cannot be null or in the past");
        }
        ScheduleRequestDTO dto = new ScheduleRequestDTO();
        dto.setAdmissionAt(admissionAt);
        AdmissionSchedules schedule = mapToEntity(dto);
        return mapToResponseDTO(scheduleRepository.save(schedule));
    }

    @Override
    public AdmissionSchedules getScheduleById(String id) {
        return scheduleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Schedule not found"));
    }

    @Override
    public List<AdmissionSchedules> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    @Override
    public ScheduleResponseDTO updateSchedule(String id, ScheduleRequestDTO dto) {
        return scheduleRepository.findById(id)
            .map(existing -> {
                AdmissionSchedules updated = mapToEntity(dto);
                updated.setId(id);
                AdmissionSchedules saved = scheduleRepository.save(updated);
                return mapToResponseDTO(saved);
            })
            .orElseThrow(() -> new RuntimeException("Schedule not found"));
    }

    @Override
    public void deleteSchedule(String id) {
        scheduleRepository.deleteById(id);
    }
//Staff phan hoi ve lich hen tu van
//    @PreAuthorize("hasAuthority('ROLE_STAFF')")
    @Override
    @Transactional
    public ScheduleResponseDTO respontStaff(String googleMeetLink, String scheduleId ) {
        Accounts user = accountUtils.getCurrentUser();
        SecurityContextHolder.getContext().getAuthentication().getName();
        AdmissionSchedules schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new RuntimeException("Schedule not found"));
        schedule.setMeetLink(googleMeetLink);
        schedule.setStatus(ProcessStatus.COMPLETED);
        schedule.setStaff(user);
        schedule.setCreateAt(LocalDateTime.now());
        return mapToResponseDTO(scheduleRepository.save(schedule));
    }
}
