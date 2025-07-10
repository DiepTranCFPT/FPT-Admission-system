package com.sba.campuses.service;


import com.sba.campuses.dto.CampusRequest;
import com.sba.campuses.pojos.Campus;

import java.util.List;

public interface CampusService {
    List<Campus> getAll();

    Campus save(CampusRequest campusRequest);

    Campus update(String id, Campus campus);

    void delete(String id);
}
