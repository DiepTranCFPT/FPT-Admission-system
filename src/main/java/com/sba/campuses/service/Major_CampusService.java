package com.sba.campuses.service;

import com.sba.campuses.pojos.Major_Campus;

import java.util.List;

public interface Major_CampusService {
    List<Major_Campus> getMajor_Campus();
    Major_Campus save(Major_Campus majorCampus);
    Major_Campus update(String id, Major_Campus majorCampus);
    void delete(String id);

}
