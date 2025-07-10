package com.sba.campuses.service;

import com.sba.campuses.dto.CampusRequest;
import com.sba.campuses.pojos.Campus;
import com.sba.campuses.repository.CampusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public Campus update(String id, Campus campus) {
        return campusRepository.save(campus);
    }

    @Override
    public void delete(String id) {
        campusRepository.deleteById(id);
    }
}
