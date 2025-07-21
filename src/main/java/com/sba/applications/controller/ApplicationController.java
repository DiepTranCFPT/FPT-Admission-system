package com.sba.applications.controller;

import com.sba.applications.dto.Scholarship;
import com.sba.applications.dto.ApplicationDTO;
import com.sba.applications.pojos.Application;
import com.sba.applications.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/create")
    public ResponseEntity<Application> createApplication(@RequestParam String idCampus , @RequestParam String idMajor) {
        try {
            ApplicationDTO dto = new ApplicationDTO();
            dto.setCampus(idCampus);
            dto.setMajor(idMajor);
            Application created = applicationService.createApplication(dto);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    @GetMapping
    public ResponseEntity<List<Application>> getAllApplications() {
        List<Application> applications = applicationService.getAllApplications();
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Application> getApplicationById(@PathVariable String id) {
        Application application = applicationService.getApplicationById(id);
        if (application != null) {
            return ResponseEntity.ok(application);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Application> updateApplication(@PathVariable String id, @RequestBody ApplicationDTO dto) {
        try {
            Application updated = applicationService.updateApplication(id, dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteApplication(@PathVariable String id) {
        applicationService.deleteApplication(id);
        return ResponseEntity.ok("Application deleted");
    }

    @PutMapping("/acceptApplication{id}")
    public ResponseEntity<String> updateApplication( @PathVariable String id) {
        applicationService.acceptApplication(id);
        return ResponseEntity.ok("Accept Application");
    }

    @PutMapping("/rejectApplication{id}")
    public ResponseEntity<String> rejectApplication(@RequestBody String response, @PathVariable String id) {
        applicationService.declineApplication(id, response);
        return ResponseEntity.ok("Reject Application");
    }

    @PostMapping("/submit-score")
    public ResponseEntity<String> submitScore(@RequestParam double scoreT,@RequestParam double scoreV,@RequestParam double scoreA) {
        try {
            double score = (scoreT + scoreV + scoreA)/3;
            Scholarship scholarship = new Scholarship(score);
            applicationService.saveScore(score);
            if (scholarship.isEligible()) {
                return ResponseEntity.ok("Congratulations! You are eligible for a scholarship.");
            } else {
                return ResponseEntity.ok("Thank you for submitting your score. Unfortunately, you are not eligible for a scholarship.");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("An error occurred while processing your score.");
        }
    }
}
