package com.sba.campuses.repository;

import com.sba.campuses.pojos.Campus;
import com.sba.campuses.pojos.Major_Campus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Major_CampusRepository extends JpaRepository<Major_Campus, String> {

    Major_Campus findByCampus(Campus campus);
}
