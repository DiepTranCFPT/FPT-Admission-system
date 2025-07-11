package com.sba.campuses.repository;

import com.sba.campuses.pojos.Major_Campus;
import com.sba.campuses.pojos.Campus;
import com.sba.campuses.pojos.Major;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface Major_CampusRepository extends JpaRepository<Major_Campus, String> {
    Optional<Major_Campus> findByMajorAndCampus(Major major, Campus campus);
}

