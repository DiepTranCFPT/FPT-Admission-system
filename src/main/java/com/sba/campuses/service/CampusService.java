package com.sba.campuses.service;


import com.sba.campuses.pojos.Campus;

import java.util.List;

public interface CampusService {
    List<Campus> getAllCampuses();

    Campus save(Campus campus);

    Campus updateCampus(Campus campus);

    void deleteCampus(String id);
}
