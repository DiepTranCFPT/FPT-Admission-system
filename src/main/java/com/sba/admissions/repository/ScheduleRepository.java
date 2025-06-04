package com.sba.admissions.repository;

import com.sba.admissions.pojos.AdmissionSchedules;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<AdmissionSchedules, String> {

}
