package com.sba.campuses.repository;

import com.sba.campuses.pojos.Campus;
import com.sba.campuses.pojos.Major;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MajorRepository extends JpaRepository<Major, String> {
    Optional<Major> findByName(String majorName);

//    List<Major> findByCampus(Campus campus);

//    @Query("SELECT * from  ")
//    List<Major> findAllMajors;

    List<Major> findByParentMajorsIsNull();

    List<Major> findByParentMajors(Major parentMajor);

    @Query("SELECT DISTINCT mc.campus FROM Major_Campus mc WHERE mc.major = :major")
    List<Campus> findCampusesByMajor(@Param("major") Major major);

    @Query("SELECT DISTINCT mc.campus FROM Major_Campus mc WHERE mc.major.id = :majorId")
    List<Campus> findCampusesByMajorId(@Param("majorId") String majorId);

}
