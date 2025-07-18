package com.sba.campuses.controller;


import com.sba.campuses.dto.ChildMajorRequest;
import com.sba.campuses.dto.MajorRequest;
import com.sba.campuses.pojos.Major;
import com.sba.campuses.service.MajorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/majors")
public class MajorController {

    @Autowired
    private MajorService majorService;

    @PostMapping
    public ResponseEntity<?> save(@RequestParam String idCampus,@RequestBody MajorRequest majorRequest) {
        try {
            Major saveMajor = majorService.save(idCampus, majorRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(saveMajor);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Major> update(@PathVariable String id, @RequestBody MajorRequest majorRequest) {
        try {
            return ResponseEntity.ok(majorService.update(id, majorRequest));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        try {
            majorService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Major>> getAll() {
        List<Major> majors = majorService.getAll();
        return ResponseEntity.ok(majors);
    }

    @GetMapping("/parents")
    public ResponseEntity<List<Major>> getAllParentMajors() {
        List<Major> parentMajors = majorService.getAllParentMajors();
        return ResponseEntity.ok(parentMajors);
    }

    @PostMapping("/child")
    public ResponseEntity<?> saveChildMajor(@RequestParam String parentMajorId,@RequestBody ChildMajorRequest childMajorRequest) {
        try {
            Major childMajor = majorService.saveChildMajor(parentMajorId,childMajorRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(childMajor);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/{majorId}/children")
    public ResponseEntity<?> getChildMajors(@PathVariable String majorId) {
        try {
            List<Major> childMajors = majorService.getChildMajors(majorId);
            return ResponseEntity.ok(childMajors);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/children")
    public ResponseEntity<List<Major>> getAllChildMajors() {
        List<Major> childMajors = majorService.getAllChildMajors();
        return ResponseEntity.ok(childMajors);
    }

    @PutMapping("/child/{id}")
    public ResponseEntity<?> updateChildMajor(@PathVariable String id, @RequestBody ChildMajorRequest request) {
        try {
            Major updatedMajor = majorService.updateChildMajor(id, request);
            return ResponseEntity.ok(updatedMajor);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }
    @GetMapping("/campus/{id}")
    public ResponseEntity<?> getMajorParentByCampus(@PathVariable String id) {
        try {
            List<Major> majors = majorService.getMajorByCampus(id);
            return ResponseEntity.ok(majors);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}
