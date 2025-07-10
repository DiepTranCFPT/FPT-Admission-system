package com.sba.campuses.controller;

import com.sba.campuses.dto.CampusRequest;
import com.sba.campuses.pojos.Campus;
import com.sba.campuses.service.CampusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/campuses")
public class CampusController {

    @Autowired
    private CampusService campusService;

    @PostMapping
    public ResponseEntity<Campus> save(@RequestBody CampusRequest campus) {
        Campus savedCampus = campusService.save(campus);
        return ResponseEntity.ok(savedCampus);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Campus> update(@PathVariable String id, @RequestBody Campus campus) {
        try {
            return ResponseEntity.ok(campusService.update(id, campus));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
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
}
