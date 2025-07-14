package com.sba.admissions.service.impl;

import com.sba.accounts.pojos.Accounts;
import com.sba.admissions.dto.ScheduleRequestDTO;
import com.sba.admissions.dto.ScheduleResponseDTO;
import com.sba.admissions.mapper.ScheduleDTO;
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
import java.util.stream.Collectors;


@Service
@Transactional
public class ScheduleServiceImpl implements ScheduleService {
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private AccountUtils accountUtils;
    @Autowired
    private EmailService emailService;
    @Autowired
    private ScheduleDTO  scheduleDTO;






//dang ky tu van bang gg meet
    @Override
    public ScheduleResponseDTO createSchedule(LocalDateTime admissionAt) {
        if(admissionAt == null || admissionAt.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Admission date cannot be null or in the past");
        }
        Accounts user = accountUtils.getCurrentUser();
        List<AdmissionSchedules> schedule1 = scheduleRepository.findByUser(user);
        int check = schedule1.stream().map(AdmissionSchedules -> ProcessStatus.IN_PROCESS).toList().size();
        int check1 = schedule1.stream()
                .map(AdmissionSchedules -> AdmissionSchedules.getAdmissionAt().isAfter(LocalDateTime.now())).toList().size();
        if(check != 0 || check1 > 0 ){
            throw new IllegalStateException("Schedule already exists");
        }

        //lich a ngay 15
        ScheduleRequestDTO dto = new ScheduleRequestDTO();
        dto.setAdmissionAt(admissionAt);
        AdmissionSchedules schedule = scheduleDTO.mapToEntity(dto);
        return scheduleDTO.mapToResponseDTO(scheduleRepository.save(schedule));
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
                AdmissionSchedules updated = scheduleDTO.mapToEntity(dto);
                updated.setId(id);
                AdmissionSchedules saved = scheduleRepository.save(updated);
                return scheduleDTO.mapToResponseDTO(saved);
            })
            .orElseThrow(() -> new RuntimeException("Schedule not found"));
    }

    @Override
    public void deleteSchedule(String id) {
        scheduleRepository.deleteById(id);
    }
    //Staff phan hoi ve lich hen tu van
    @Override
    @Transactional
    public ScheduleResponseDTO responseStaff(String googleMeetLink, String scheduleId ) {
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
        emailDetail.setRecipient(schedule.getUser().getEmail()); // email sinh viên
        emailDetail.setSubject("Lịch hẹn tư vấn tuyển sinh FPTU");
        emailDetail.setName(user.getUsername()); // tên nhân viên phụ trách
        emailDetail.setLink(googleMeetLink);
        emailDetail.setExtra(extra);

        emailDetail.setTemplate("schedule-meeting-template");

        new Thread(() -> emailService.sendMailTemplate(emailDetail)).start();

        return scheduleDTO.mapToResponseDTO(updatedSchedule);
    }

}
