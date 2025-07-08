package com.sba.campuses.service;

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
    public List<Campus> getAllCampuses() {
        return campusRepository.findAll();
    }

    @Override
    public Campus save(Campus campus) {
        return campusRepository.save(campus);
    }

    @Override
    public Campus updateCampus(Campus campus) {
        return campusRepository.save(campus);
    }

    @Override
    public void deleteCampus(String id) {
        campusRepository.deleteById(id);
    }
}
