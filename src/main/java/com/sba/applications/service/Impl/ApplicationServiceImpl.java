package com.sba.applications.service.Impl;

import com.sba.accounts.pojos.Accounts;
import com.sba.applications.dto.ApplicationDTO;
import com.sba.applications.pojos.Application;
import com.sba.applications.repository.ApplicationRepository;
import com.sba.campuses.pojos.Major;
import com.sba.campuses.repository.CampusRepository;
import com.sba.campuses.repository.MajorRepository;
import com.sba.campuses.repository.Major_CampusRepository;
import com.sba.enums.ApplicationStatus;
import com.sba.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ApplicationServiceImpl implements com.sba.applications.service.ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final CampusRepository campusRepository;
    private final MajorRepository majorRepository;
    private final Major_CampusRepository majorCampusRepository;
    private final AccountUtils accountUtils;

    private ApplicationServiceImpl (ApplicationRepository applicationRepository, CampusRepository campusRepository, MajorRepository majorRepository, Major_CampusRepository majorCampusRepository, AccountUtils accountUtils){
        this.applicationRepository = applicationRepository;
        this.campusRepository = campusRepository;
        this.majorRepository = majorRepository;
        this.majorCampusRepository = majorCampusRepository;
        this.accountUtils = accountUtils;
    }

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
        var major = majorRepository.findByName(dto.getMajor())
                .orElseThrow(() -> new IllegalArgumentException("Major not found: " + dto.getMajor()));
        var campus = campusRepository.findByName(dto.getCampus())
                .orElseThrow(() -> new IllegalArgumentException("Campus not found: " + dto.getCampus()));
        boolean exists = majorCampusRepository.findByMajorAndCampus(major, campus).isPresent();
        if (!exists) {
            throw new IllegalArgumentException("The selected major is not offered at the selected campus");
        }

        Application application = new Application();
        application.setAccounts(user);
        application.setCampus(campus);
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
                .filter(Application::isDeleted)
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
        validateApplicationDTO(applicationDTO);
        var major = majorRepository.findByName(applicationDTO.getMajor())
                .orElseThrow(() -> new IllegalArgumentException("Major not found: " + applicationDTO.getMajor()));
        var campus = campusRepository.findByName(applicationDTO.getCampus())
                .orElseThrow(() -> new IllegalArgumentException("Campus not found: " + applicationDTO.getCampus()));
        existing.setMajor(major);
        existing.setCampus(campus);
        return applicationRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteApplication(String id) {
        if (!applicationRepository.existsById(id)) {
            throw new IllegalArgumentException("Application not found");
        }
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));
        if(application.isDeleted()) {
            throw new SecurityException("Application has been deleted");
        }
        application.setDeleted(true);
        applicationRepository.save(application);
    }
}
