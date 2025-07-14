package com.sba.campuses.service;


import com.sba.campuses.dto.CampusRequest;
import com.sba.campuses.dto.ChildMajorRequest;
import com.sba.campuses.pojos.Campus;
import com.sba.campuses.pojos.Major;

import java.util.List;

public interface CampusService {
    List<Campus> getAll();

    Campus save(CampusRequest campusRequest);

    Campus update(String id, CampusRequest campusRequest);

    void delete(String id);

    Campus getById(String id);

    List<Campus> search(String keyword);

    List<Major> getMajorsByCampus(Campus campus);

    List<Major> getMajorsByCampusId(String campusId);
}
