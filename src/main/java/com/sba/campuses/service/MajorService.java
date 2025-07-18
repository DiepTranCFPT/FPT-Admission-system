package com.sba.campuses.service;

import com.sba.campuses.dto.ChildMajorRequest;
import com.sba.campuses.dto.MajorRequest;
import com.sba.campuses.pojos.Campus;
import com.sba.campuses.pojos.Major;

import java.util.List;

public interface MajorService {
    List<Major> getAll();

    Major save(String id ,MajorRequest majorRequest);

    Major update(String id, MajorRequest majorRequest);

    void delete(String id);

    Major getbyId(String id);

    List<Major> getAllParentMajors();

    Major saveChildMajor(String id ,ChildMajorRequest childMajorRequest);

    List<Major> getChildMajors(String majorId);

    List<Major> getAllChildMajors();
    Major updateChildMajor(String id, ChildMajorRequest request);

    List<Campus> getCampusesByMajor(Major major);

    List<Campus> getCampusesByMajorId(String majorId);

}
