package com.sba.campuses.controller;


import com.sba.campuses.pojos.Major;
import com.sba.campuses.service.MajorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/majors")
public class MajorController {

    @Autowired
    private MajorService majorService;

    @PostMapping
    public ResponseEntity<Major> save(@RequestBody Major major) {
        Major savedMajor = majorService.save(major);
        return ResponseEntity.ok(savedMajor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Major> update(@PathVariable String id, @RequestBody Major major) {
        try {
            return ResponseEntity.ok(majorService.update(id, major));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        majorService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Major>> getAll() {
        List<Major> majors = majorService.getAll();
        return ResponseEntity.ok(majors);
    }

}
