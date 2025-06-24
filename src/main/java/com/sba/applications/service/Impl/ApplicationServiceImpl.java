package com.sba.applications.service.Impl;

import com.sba.accounts.pojos.Accounts;
import com.sba.applications.dto.ApplicationDTO;
import com.sba.applications.pojos.Application;
import com.sba.applications.repository.ApplicationRepository;
import com.sba.campuses.pojos.Major;
import com.sba.campuses.repository.CampusRepository;
import com.sba.campuses.repository.MajorRepository;
import com.sba.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements com.sba.applications.service.ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final CampusRepository campusRepository;
    private final MajorRepository majorRepository;
    private final AccountUtils accountUtils;

    @Override
    @Transactional
    public Application createApplication(ApplicationDTO applicationDTO) {
        validateApplicationDTO(applicationDTO);
        Accounts currentUser = getAuthenticatedUser();
        ensureNoExistingApplication(currentUser);
        Application newApplication = buildApplication(applicationDTO, currentUser);
        return applicationRepository.save(newApplication);
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

        Application application = new Application();
        application.setAccounts(user);
        application.setCampus(campus);
        List<Major> majors = majorRepository.findByCampus(campusRepository.findByName(dto.getCampus()).orElseThrow(() -> new IllegalArgumentException("Campus not found")));
        for (Major majorMajor : majors) {
            if (!majorMajor.getName().equals(dto.getMajor())) {
                throw new IllegalArgumentException("The selected campus does not match the major's campus");
            }
        }
        application.setMajor(major);
        application.setScholarship("null");

        return application;
    }
}
