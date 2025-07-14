package com.sba.campuses.repository;

import com.sba.campuses.pojos.Campus;
import com.sba.campuses.pojos.Major;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CampusRepository extends JpaRepository<Campus, String> {

    Optional<Campus> findByName(String campusName);

    List<Campus> findByNameContainingIgnoreCase(String name);
    @Query("SELECT DISTINCT mc.major FROM Major_Campus mc WHERE mc.campus = :campus")
    List<Major> findMajorsByCampus(@Param("campus") Campus campus);

    @Query("SELECT DISTINCT mc.major FROM Major_Campus mc WHERE mc.campus.id = :campusId")
    List<Major> findMajorsByCampusId(@Param("campusId") String campusId);
}
