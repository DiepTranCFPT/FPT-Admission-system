package com.sba.campuses.repository;

import com.sba.campuses.pojos.Major_Campus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Major_CampusRepository extends JpaRepository<Major_Campus, String> {

    // Define any custom query methods if needed
    // For example, you might want to find majors by campus or vice versa
    // Optional<Major_Campus> findByCampusName(String campusName);
    // List<Major_Campus> findByMajorName(String majorName);
}
