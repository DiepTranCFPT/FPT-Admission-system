package com.sba.campuses.service;

import com.sba.campuses.dto.CampusRequest;
import com.sba.campuses.pojos.Campus;
import com.sba.campuses.pojos.Major;
import com.sba.campuses.pojos.Major_Campus;
import com.sba.campuses.repository.CampusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CampusServiceImp implements CampusService {
    @Autowired
    private CampusRepository campusRepository;

    @Override
    public List<Campus> getAll() {
        return campusRepository.findAll();
    }

    @Override
    public Campus save(CampusRequest campusRequest) {
        Campus campus = new Campus();
        campus.setName(campusRequest.getName());
        campus.setAddress(campusRequest.getAddress());
        campus.setPhone(campusRequest.getPhone());
        campus.setEmail(campusRequest.getEmail());
        campus.setDeleted(false);
        return campusRepository.save(campus);
    }

    @Override
    public Campus update(String id, CampusRequest campusRequest) {
        Campus campus = campusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campus not found with id: " + id));
        campus.setName(campusRequest.getName());
        campus.setAddress(campusRequest.getAddress());
        campus.setPhone(campusRequest.getPhone());
        campus.setEmail(campusRequest.getEmail());
        return campusRepository.save(campus);
    }

    @Override
    public void delete(String id) {
        campusRepository.deleteById(id);
    }

    @Override
    public Campus getById(String id) {
        return campusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campus not found with id: " + id));
    }

    @Override
    public List<Campus> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAll();
        }

        return campusRepository.findByNameContainingIgnoreCase(keyword);
    }

    @Override
    public List<Major> getMajorsByCampus(Campus campus) {
        return campus.getMajor_campuses().stream()
                .map(Major_Campus::getMajor)
                .collect(Collectors.toList());
    }

    @Override
    public List<Major> getMajorsByCampusId(String campusId) {
        return campusRepository.findMajorsByCampusId(campusId);
    }

}
