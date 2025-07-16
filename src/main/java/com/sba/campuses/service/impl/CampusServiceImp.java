package com.sba.campuses.service.impl;

import com.sba.campuses.dto.CampusRequest;
import com.sba.campuses.pojos.Campus;
import com.sba.campuses.pojos.Major;
import com.sba.campuses.pojos.Major_Campus;
import com.sba.campuses.repository.CampusRepository;
import com.sba.campuses.repository.Major_CampusRepository;
import com.sba.campuses.service.CampusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CampusServiceImp implements CampusService {
    @Autowired
    private CampusRepository campusRepository;

    @Autowired
    private Major_CampusRepository  majorCampusRepository;

    @Override
    public List<Campus> getAll() {
        return campusRepository.findAll()
                .stream()
                .filter(Campus -> !Campus.isDeleted())
                .toList();
    }

    @Override
    public Campus save(CampusRequest campusRequest) {
        Campus campus = new Campus();
        if(campusRepository.findByName(campusRequest.getName()).isPresent()){
            throw new IllegalArgumentException("Campus is present");
        }
        campus.setName(campusRequest.getName());
        campus.setAddress(campusRequest.getAddress());
        campus.setPhone(campusRequest.getPhone());
        campus.setEmail(campusRequest.getEmail());
        campusRepository.save(campus);

        Major_Campus majorCampus = new Major_Campus();
        majorCampus.setCampus(campus);
        majorCampusRepository.save(majorCampus);

        return campus;
    }

    @Override
    public Campus update(String id, CampusRequest campusRequest) {
        Campus campus = campusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campus not found with id: " + id));
        if(!Objects.equals(campus.getName(), campusRequest.getName())){
            campus.setName(campusRequest.getName());
        }
        if(!Objects.equals(campus.getAddress(), campusRequest.getAddress())){
            campus.setAddress(campusRequest.getAddress());
        }
        if(!Objects.equals(campus.getPhone(), campusRequest.getPhone())){
            campus.setPhone(campusRequest.getPhone());
        }
        if(!Objects.equals(campus.getEmail(), campusRequest.getEmail())){
            campus.setEmail(campusRequest.getEmail());
        }
        return campusRepository.save(campus);
    }

    @Override
    public void delete(String id) {
        Campus campus = campusRepository.findById(id).orElseThrow(() -> new RuntimeException("Campus not found with id: " + id));
        if(campus.isDeleted()){
            throw new IllegalArgumentException("Campus is deleted");
        }
        campus.setDeleted(true);
        campusRepository.save(campus);
    }

    @Override
    public Campus getById(String id) {
        Campus campus = campusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campus not found with id: " + id));
        if(campus.isDeleted()){
            throw new IllegalArgumentException("Campus is deleted");
        }
        return campus;
    }

    @Override
    public List<Campus> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAll();
        }
        return campusRepository.findByNameContainingIgnoreCase(keyword)
                .stream()
                .filter(s-> !s.isDeleted())
                .toList();
    }

    @Override
    public List<Major> getMajorsByCampus(Campus campus) {
        return campus.getMajor_campuses().stream()
                .map(Major_Campus::getMajor)
                .collect(Collectors.toList());
    }

    @Override
    public List<Major> getMajorsByCampusId(String campusId) {
        List<Major> majors = campusRepository.findMajorsByCampusId(campusId);
        return majors.stream().filter(Major -> !Major.isDeleted()).collect(Collectors.toList());
    }

}
