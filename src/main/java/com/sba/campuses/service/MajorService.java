package com.sba.campuses.service;

import com.sba.campuses.pojos.Major;

import java.util.List;

public interface MajorService {
     List<Major> getAllMajors();
     Major save(Major major);
     Major updateMajor(Major major);
     void deleteMajor(String id);
}
