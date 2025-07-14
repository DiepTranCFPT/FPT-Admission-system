package com.sba.campuses.controller;

import com.sba.campuses.dto.CampusRequest;
import com.sba.campuses.pojos.Campus;
import com.sba.campuses.service.CampusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/campuses")
public class CampusController {

    @Autowired
    private CampusService campusService;

    @PostMapping
    public ResponseEntity<?> save(@RequestBody CampusRequest request) {
        try {
            Campus savedCampus = campusService.save(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCampus);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody CampusRequest campusRequest) {
        try {
            Campus updatedCampus = campusService.update(id, campusRequest);
            return ResponseEntity.ok(updatedCampus);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        campusService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Campus>> findAll() {
        List<Campus> campuses = campusService.getAll();
        return ResponseEntity.ok(campuses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable String id) {
        try {
            Campus campus = campusService.getById(id);
            return ResponseEntity.ok(campus);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Campus>> search(@RequestParam(required = false) String keyword) {
        List<Campus> results = campusService.search(keyword);
        return ResponseEntity.ok(results);
    }
}
