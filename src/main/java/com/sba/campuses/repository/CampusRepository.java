package com.sba.campuses.repository;

import com.sba.campuses.pojos.Campus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CampusRepository extends JpaRepository<Campus, String> {

    Optional<Campus> findByName(String campusName);
}
