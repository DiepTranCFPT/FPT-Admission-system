package com.sba.admissions.service.impl;

import com.sba.accounts.pojos.Accounts;
import com.sba.admissions.dto.ScheduleRequestDTO;
import com.sba.admissions.dto.ScheduleResponseDTO;
import com.sba.admissions.pojos.AdmissionSchedules;
import com.sba.admissions.repository.ScheduleRepository;
import com.sba.admissions.service.ScheduleService;
import com.sba.authentications.services.EmailService;
import com.sba.enums.ProcessStatus;
import com.sba.model.EmailDetail;
import com.sba.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ScheduleServiceImpl implements ScheduleService {
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private AccountUtils accountUtils;

    @Autowired
    private EmailService emailService;

    private AdmissionSchedules mapToEntity(ScheduleRequestDTO dto) {
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

        AdmissionSchedules schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        // Cập nhật schedule
        schedule.setMeetLink(googleMeetLink);
        schedule.setStatus(ProcessStatus.COMPLETED);
        schedule.setStaff(user);
        schedule.setCreateAt(LocalDateTime.now());

        AdmissionSchedules updatedSchedule = scheduleRepository.save(schedule);

        // === GỬI EMAIL ===
        Map<String, Object> extra = new HashMap<>();
        extra.put("schedule", updatedSchedule);

        EmailDetail emailDetail = new EmailDetail();
        emailDetail.setRecipient(updatedSchedule.getUser().getEmail()); // email sinh viên
        emailDetail.setSubject("Lịch hẹn tư vấn tuyển sinh FPTU");
        emailDetail.setName(user.getUsername()); // tên nhân viên phụ trách
        emailDetail.setLink(googleMeetLink);
        emailDetail.setExtra(extra);

        emailDetail.setTemplate("schedule-meeting-template");

        new Thread(() -> emailService.sendMailTemplate(emailDetail)).start();

        return mapToResponseDTO(updatedSchedule);
    }

}
