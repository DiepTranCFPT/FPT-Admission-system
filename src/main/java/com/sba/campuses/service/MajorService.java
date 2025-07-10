package com.sba.campuses.service;

import com.sba.campuses.pojos.Major;

import java.util.List;

public interface MajorService {
    List<Major> getAll();

    Major save(Major major);

    Major update(String id, Major major);

    void delete(String id);
}
