package com.sba.campuses.controller;


import com.sba.campuses.pojos.Major_Campus;
import com.sba.campuses.service.Major_CampusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/majors_campuses")
public class Major_CampusController {

    @Autowired
    private Major_CampusService majorCampusService;

    @PostMapping
    public ResponseEntity<Major_Campus> save(Major_Campus majorCampus) {
        Major_Campus savedMajorCampus = majorCampusService.save(majorCampus);
        return ResponseEntity.ok(savedMajorCampus);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Major_Campus> update(String id, Major_Campus majorCampus) {
        try {
            return ResponseEntity.ok(majorCampusService.update(id, majorCampus));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(String id) {
        majorCampusService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Major_Campus>> getAll() {
        List<Major_Campus> majorCampuses = majorCampusService.getMajor_Campus();
        return ResponseEntity.ok(majorCampuses);
    }
}
