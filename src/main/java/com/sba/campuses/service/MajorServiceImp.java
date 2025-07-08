package com.sba.campuses.service;

import com.sba.campuses.pojos.Major;
import com.sba.campuses.repository.MajorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MajorServiceImp implements MajorService {

    @Autowired
    private MajorRepository majorRepository;

    @Override
    public List<Major> getAllMajors() {
        return majorRepository.findAll();
    }

    @Override
    public Major save(Major major) {
        return majorRepository.save(major);
    }

    @Override
    public Major updateMajor(Major major) {
        return majorRepository.save(major);
    }

    @Override
    public void deleteMajor(String id) {
        majorRepository.deleteById(id);
    }
}
