package com.sba.campuses.repository;


import com.sba.campuses.pojos.Major;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MajorRepository extends JpaRepository<Major, String> {
    Optional<Major> findByName(String majorName);
}
