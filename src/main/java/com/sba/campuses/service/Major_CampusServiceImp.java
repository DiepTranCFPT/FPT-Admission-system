package com.sba.campuses.service;

import com.sba.campuses.pojos.Major_Campus;
import com.sba.campuses.repository.MajorRepository;
import com.sba.campuses.repository.Major_CampusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Major_CampusServiceImp implements Major_CampusService {

    @Autowired
    private Major_CampusRepository major_campusRepository;

    @Override
    public List<Major_Campus> getMajor_Campus() {
        return major_campusRepository.findAll();
    }

    @Override
    public Major_Campus save(Major_Campus majorCampus) {
        return major_campusRepository.save(majorCampus);
    }

    @Override
    public Major_Campus update(String id, Major_Campus majorCampus) {
        return major_campusRepository.save(majorCampus);
    }

    @Override
    public void delete(String id) {
        major_campusRepository.deleteById(id);
    }
}
