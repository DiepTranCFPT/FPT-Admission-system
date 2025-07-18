package com.sba.applications.service.Impl;

import com.sba.accounts.pojos.Accounts;
import com.sba.applications.dto.ApplicationDTO;
import com.sba.applications.pojos.Application;
import com.sba.applications.repository.ApplicationRepository;
import com.sba.applications.service.ApplicationService;
import com.sba.authentications.services.EmailService;
import com.sba.campuses.pojos.Campus;
import com.sba.campuses.pojos.Major;
import com.sba.campuses.repository.CampusRepository;
import com.sba.campuses.repository.MajorRepository;
import com.sba.enums.ApplicationStatus;
import com.sba.model.EmailDetail;
import com.sba.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final CampusRepository campusRepository;
    private final MajorRepository majorRepository;
    private final AccountUtils accountUtils;
    private final EmailService emailService;


    private void validateApplicationDTO(ApplicationDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Application data must not be null");
        }
        if (dto.getMajor() == null || dto.getMajor().isBlank()) {
            throw new IllegalArgumentException("Major is required");
        }
        if (dto.getCampus() == null || dto.getCampus().isBlank()) {
            throw new IllegalArgumentException("Campus is required");
        }
    }
    private Accounts getAuthenticatedUser() {
        return Optional.ofNullable(accountUtils.getCurrentUser())
                .orElseThrow(() -> new SecurityException("User is not authenticated"));
    }
    private void ensureNoExistingApplication(Accounts user) {
        if (applicationRepository.findByAccounts(user).isPresent()) {
            throw new IllegalStateException("An application already exists for this user");
        }
    }

    private Application buildApplication(ApplicationDTO dto, Accounts user) {
        Major major = majorRepository.findById(dto.getMajor())
                .orElseThrow(() -> new IllegalArgumentException("Major not found: " + dto.getMajor()));
        Campus campus = campusRepository.findById(dto.getCampus())
                .orElseThrow(() -> new IllegalArgumentException("Campus not found: " + dto.getCampus()));

        Application application = new Application();
        application.setAccounts(user);
        application.setCampus(campus);
        List<Major> majors = campusRepository.findMajorsByCampus(campusRepository.findById(
                dto.getCampus()).orElseThrow(() -> new IllegalArgumentException("Campus not found By Campus")));

        for (Major majorMajor : majors) {
            if (!majorMajor.getId().equals(dto.getMajor())) {
                throw new IllegalArgumentException("The selected campus does not match the major's campus");
            }
        }
        application.setMajor(major);
        application.setScholarship("null");
        application.setApplicationStatus(ApplicationStatus.PENDING);
        return application;
    }
    @Override
    @Transactional
    public Application createApplication(ApplicationDTO applicationDTO) {
        validateApplicationDTO(applicationDTO);
        Accounts currentUser = getAuthenticatedUser();
        ensureNoExistingApplication(currentUser);
        Application newApplication = buildApplication(applicationDTO, currentUser);
        return applicationRepository.save(newApplication);
    }


    @Override
    public List<Application> getAllApplications() {
        return applicationRepository.findAll()
                .stream()
                .filter(Application -> !Application.isDeleted())
                .toList();
    }

    @Override
    public Application getApplicationById(String id) {
       Application application = applicationRepository.findById(id).orElseThrow(()-> new RuntimeException("Application not found with id: " + id));
       if (application.isDeleted()) {
           throw new RuntimeException("Application has been deleted");
       }
        return application;
    }

    @Override
    @Transactional
    public Application updateApplication(String id, ApplicationDTO applicationDTO) {
        Application existing = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));
        if(existing.isDeleted()){
            throw new SecurityException("Application has been deleted");
        }
        validateApplicationDTO(applicationDTO);
        Major major = majorRepository.findByName(applicationDTO.getMajor())
                .orElseThrow(() -> new IllegalArgumentException("Major not found: " + applicationDTO.getMajor()));
        Campus campus = campusRepository.findByName(applicationDTO.getCampus())
                .orElseThrow(() -> new IllegalArgumentException("Campus not found: " + applicationDTO.getCampus()));
        existing.setMajor(major);
        existing.setCampus(campus);
        return applicationRepository.save(existing);
    }
    @Override
    @Transactional
    public void deleteApplication(String id) {
        if (!applicationRepository.existsById(id) || Objects.requireNonNull(applicationRepository.findById(id).orElse(null)).isDeleted()) {
            throw new IllegalArgumentException("Application not found or deleted");
        }
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));
        application.setDeleted(true);
        applicationRepository.save(application);
    }

    @Override
    public void acceptApplication(String id) {
        Application application = applicationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Application not found"));
        if(!application.getApplicationStatus().equals(ApplicationStatus.PENDING)){
            throw new SecurityException("Application has been is present");
        }
        application.setApplicationStatus(ApplicationStatus.APPROVED);

        EmailDetail emailDetail = new EmailDetail();
        emailDetail.setRecipient(application.getAccounts().getEmail());
        emailDetail.setSubject("Your Application Has Been Accepted");
        emailDetail.setName(application.getAccounts().getUsername());
        emailDetail.setTemplate("accept-application-template");
        // Set extra variables for template
        Map<String, Object> extra = new HashMap<>();
        extra.put("major", application.getMajor().getName());
        extra.put("campus", application.getCampus().getName());
        extra.put("applicantName", application.getAccounts().getUsername());
        emailDetail.setExtra(extra);
        // Send email with template
        emailService.sendMailTemplate(emailDetail);

        applicationRepository.save(application);
    }

    @Override
    public void declineApplication(String id, String response) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));
        if(!application.getApplicationStatus().equals(ApplicationStatus.PENDING)){
            throw new SecurityException("Application has been is present");
        }
        application.setApplicationStatus(ApplicationStatus.REJECTED);
        EmailDetail emailDetail = new EmailDetail();
        emailDetail.setRecipient(application.getAccounts().getEmail());
        emailDetail.setSubject("Your Application Has Been Rejected");
        emailDetail.setName(application.getAccounts().getUsername());
        emailDetail.setTemplate("reject-application-template");
        Map<String, Object> extra = new HashMap<>();
        extra.put("major", application.getMajor().getName());
        extra.put("campus", application.getCampus().getName());
        extra.put("reason", response);
        extra.put("applicantName", application.getAccounts().getUsername());
        emailDetail.setExtra(extra);
        // Send email with template
        emailService.sendMailTemplate(emailDetail);
        applicationRepository.save(application);
    }

}
