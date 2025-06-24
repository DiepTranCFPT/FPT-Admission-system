package com.sba.admissions.service.impl;

import com.sba.admissions.dto.ScheduleRequestDTO;
import com.sba.admissions.dto.ScheduleResponseDTO;
import com.sba.admissions.pojos.AdmissionSchedules;
import com.sba.admissions.repository.ScheduleRepository;
import com.sba.admissions.service.ScheduleService;
import com.sba.enums.ProcessStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    @Autowired
    private ScheduleRepository scheduleRepository;

    private AdmissionSchedules mapToEntity(ScheduleRequestDTO dto) {
        AdmissionSchedules schedule = new AdmissionSchedules();
        schedule.setCreateAt(dto.getCreateAt());
        schedule.setAdmissionAt(dto.getAdmissionAt());
        schedule.setStatus(dto.getStatus() != null ? ProcessStatus.valueOf(dto.getStatus()) : null);
        schedule.setMeetLink(dto.getMeetLink());
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

    @Override
    public ScheduleResponseDTO createSchedule(ScheduleRequestDTO dto) {
        AdmissionSchedules schedule = mapToEntity(dto);
        AdmissionSchedules saved = scheduleRepository.save(schedule);
        return mapToResponseDTO(saved);
    }

    @Override
    public ScheduleResponseDTO getScheduleById(String id) {
        return scheduleRepository.findById(id)
            .map(this::mapToResponseDTO)
            .orElseThrow(() -> new RuntimeException("Schedule not found"));
    }

    @Override
    public List<ScheduleResponseDTO> getAllSchedules() {
        return scheduleRepository.findAll().stream().map(this::mapToResponseDTO).toList();
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
}
